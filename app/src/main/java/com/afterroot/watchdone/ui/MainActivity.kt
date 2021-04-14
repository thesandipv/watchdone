/*
 * Copyright (C) 2020-2021 Sandip Vaghela
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.afterroot.watchdone.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.transition.AutoTransition
import com.afollestad.materialdialogs.MaterialDialog
import com.afterroot.core.extensions.getDrawableExt
import com.afterroot.core.extensions.progress
import com.afterroot.core.extensions.visible
import com.afterroot.core.network.NetworkState
import com.afterroot.core.onVersionGreaterThanEqualTo
import com.afterroot.tmdbapi2.repository.ConfigRepository
import com.afterroot.watchdone.BuildConfig
import com.afterroot.watchdone.base.Constants.RC_PERMISSION
import com.afterroot.watchdone.R
import com.afterroot.watchdone.base.Collection
import com.afterroot.watchdone.base.Field
import com.afterroot.watchdone.data.model.User
import com.afterroot.watchdone.databinding.ActivityMainBinding
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.utils.FirebaseUtils
import com.afterroot.watchdone.utils.PermissionChecker
import com.afterroot.watchdone.utils.hideKeyboard
import com.afterroot.watchdone.viewmodel.NetworkViewModel
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import org.jetbrains.anko.design.indefiniteSnackbar
import org.jetbrains.anko.design.snackbar
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val manifestPermissions = arrayOf(Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val settings: Settings by inject()
    private val firebaseUtils: FirebaseUtils by inject()
    private val networkViewModel: NetworkViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
    }

    override fun onStart() {
        super.onStart()
        if (get<FirebaseAuth>().currentUser == null) { // If not logged in, go to login.
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        } else initialize()
        get<FirebaseAuth>().addAuthStateListener {
            if (get<FirebaseAuth>().currentUser == null) { // If not logged in, go to login.
                startActivity(Intent(applicationContext, SplashActivity::class.java))
                finish()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun initialize() {
        if (settings.isFirstInstalled) {
            Bundle().apply {
                putString("Device_Name", Build.DEVICE)
                putString("Device_Model", Build.MODEL)
                putString("Manufacturer", Build.MANUFACTURER)
                putString("AndroidVersion", Build.VERSION.RELEASE)
                putString("AppVersion", BuildConfig.VERSION_CODE.toString())
                putString("Package", BuildConfig.APPLICATION_ID)
                FirebaseAnalytics.getInstance(this@MainActivity).logEvent("DeviceInfo", this)
            }
            settings.isFirstInstalled = false
        }

        if (settings.baseUrl == null) {
            lifecycleScope.launch {
                settings.baseUrl = get<ConfigRepository>().getConfig().imagesConfig?.secureBaseUrl
            }
        }
        if (settings.posterSizes == null) {
            lifecycleScope.launch {
                val set = mutableSetOf<String>()
                try {
                    get<ConfigRepository>().getConfig().imagesConfig?.posterSizes?.map {
                        set.add(it)
                    }
                } catch (e: Exception) {
                } finally {
                    settings.posterSizes = set
                }
            }
        }
        // Initialize AdMob SDK
        MobileAds.initialize(this) {
        }

        onVersionGreaterThanEqualTo(
            Build.VERSION_CODES.M,
            {
                checkPermissions() // Load Fragments after checking permissions
            }
        )

        setUpNavigation()

        // Add user in db if not available
        addUserInfoInDB()
        setUpNetworkObserver()
    }

    private var dialog: MaterialDialog? = null
    private fun setUpNetworkObserver() {
        networkViewModel.doIfNetworkConnected(
            this,
            doWhenConnected = {
                if (dialog != null && dialog?.isShowing!!) dialog?.dismiss()
            },
            doWhenNotConnected = {
                dialog = showNetworkDialog(it)
            }
        )
    }

    private fun showNetworkDialog(state: NetworkState) = MaterialDialog(this).show {
        title = if (state == NetworkState.CONNECTION_LOST) "Connection Lost" else "Network Disconnected"
        cancelable(false)
        message(text = "Please check your network connection")
        negativeButton(text = "Exit") {
            finish()
        }
        positiveButton(text = "Retry") {
            setUpNetworkObserver()
        }
    }

    /**
     * Add user info in FireStore Database
     */
    private fun addUserInfoInDB() {
        try {
            val curUser = firebaseUtils.firebaseUser
            val userRef = get<FirebaseFirestore>().collection(Collection.USERS).document(curUser!!.uid)
            get<FirebaseMessaging>().token
                .addOnCompleteListener(
                    OnCompleteListener { tokenTask ->
                        if (!tokenTask.isSuccessful) {
                            return@OnCompleteListener
                        }
                        userRef.get().addOnCompleteListener { getUserTask ->
                            if (getUserTask.isSuccessful) {
                                if (!getUserTask.result!!.exists()) {
                                    binding.container.snackbar("User not available. Creating User..").anchorView =
                                        binding.toolbar
                                    val user = User(curUser.displayName, curUser.email, curUser.uid, tokenTask.result)
                                    userRef.set(user).addOnCompleteListener { setUserTask ->
                                        if (!setUserTask.isSuccessful) Log.e(
                                            TAG,
                                            "Can't create firebaseUser",
                                            setUserTask.exception
                                        )
                                    }
                                } else if (getUserTask.result!![Field.FCM_ID] != tokenTask.result) {
                                    userRef.update(Field.FCM_ID, tokenTask.result)
                                }
                            } else Log.e(TAG, "Unknown Error", getUserTask.exception)
                        }
                    }
                )
        } catch (e: Exception) {
            Log.e(TAG, "addUserInfoInDB: $e")
        }
    }

    private fun checkPermissions() {
        val permissionChecker = PermissionChecker(this)
        if (permissionChecker.lacksPermissions(manifestPermissions)) { // missing permissions
            ActivityCompat.requestPermissions(this, manifestPermissions, RC_PERMISSION)
        } else { // no missing permissions
            setUpNavigation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RC_PERMISSION -> {
                val isPermissionNotGranted =
                    grantResults.isNotEmpty() && grantResults.any { it == PackageManager.PERMISSION_DENIED }
                if (isPermissionNotGranted) {
                    binding.container.indefiniteSnackbar(
                        getString(R.string.msg_grant_app_permissions),
                        getString(R.string.text_action_grant)
                    ) {
                        checkPermissions()
                    }.anchorView = binding.toolbar
                } else {
                    setUpNavigation()
                }
            }
        }
    }

    private fun setUpNavigation() {
        val drawerToggle = DrawerArrowDrawable(this)
        navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.toolbar.apply {
                performShow()
                hideOnScroll = true
                navigationIcon = drawerToggle
                setNavigationOnClickListener {
                    navController.navigateUp()
                }
            }
            this.hideKeyboard(binding.root)
            when (destination.id) {
                R.id.navigation_home -> {
                    setTitle(getString(R.string.title_watchlist))
                    binding.fab.apply {
                        show()
                        setOnClickListener { navController.navigate(R.id.toSearchNew) }
                        setImageDrawable(context.getDrawableExt(R.drawable.ic_search))
                    }
                    drawerToggle.apply {
                        if (progress == 1f) progress(1f, 0f) // As hamburger
                    }
                    binding.toolbar.apply {
                        setNavigationOnClickListener {
                            BottomNavDrawerFragment().apply {
                                show(supportFragmentManager, this.tag)
                            }
                        }
                        fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
                    }
                }

                R.id.navigation_settings -> {
                    setTitle(getString(R.string.title_settings))
                    binding.fab.hide()
                    drawerToggle.progress(0f, 1f) // As back arrow
                }
                R.id.navigation_edit_profile -> {
                    setTitle(getString(R.string.title_edit_profile))
                    binding.fab.show()
                    drawerToggle.progress(0f, 1f) // As back arrow
                }
                R.id.navigation_movie_info -> {
                    setTitle(null)
                    binding.titleLayout.visible(false)
                    binding.fab.hide()
                    drawerToggle.progress(0f, 1f) // As back arrow
                }
                R.id.navigation_tv_info -> {
                    setTitle(null)
                    binding.titleLayout.visible(false)
                    binding.fab.hide()
                    drawerToggle.progress(0f, 1f) // As back arrow
                }
                R.id.navigation_discover -> {
                    setTitle(getString(R.string.text_discover))
                    binding.fab.hide()
                    drawerToggle.progress(0f, 1f) // As back arrow
                }
                R.id.navigation_search_new -> {
                    binding.titleLayout.visible(false, AutoTransition())
                    setTitle(null)
                    binding.fab.hide()
                    drawerToggle.progress(0f, 1f) // As back arrow
                }
            }
        }
    }

    private fun setTitle(title: String?) {
        binding.apply {
            val params = navHostFragment.layoutParams as CoordinatorLayout.LayoutParams
            if (title.isNullOrBlank()) {
                params.behavior = null
                titleLayout.visible(false)
            } else {
                params.behavior = AppBarLayout.ScrollingViewBehavior()
                this.title = title
                titleLayout.visible(true)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
