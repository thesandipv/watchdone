/*
 * Copyright (C) 2020-2023 Sandip Vaghela
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
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.transition.AutoTransition
import com.afterroot.data.utils.FirebaseUtils
import com.afterroot.tmdbapi.repository.ConfigRepository
import com.afterroot.ui.common.compose.theme.Theme
import com.afterroot.utils.extensions.getDrawableExt
import com.afterroot.utils.extensions.progress
import com.afterroot.utils.extensions.visible
import com.afterroot.utils.onVersionGreaterThanEqualTo
import com.afterroot.watchdone.BuildConfig
import com.afterroot.watchdone.R
import com.afterroot.watchdone.base.Collection
import com.afterroot.watchdone.base.Constants.RC_PERMISSION
import com.afterroot.watchdone.base.Field
import com.afterroot.watchdone.data.model.LocalUser
import com.afterroot.watchdone.databinding.ActivityMainBinding
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.common.showNetworkDialog
import com.afterroot.watchdone.ui.home.Home
import com.afterroot.watchdone.ui.settings.SettingsActivity
import com.afterroot.watchdone.utils.PermissionChecker
import com.afterroot.watchdone.utils.hideKeyboard
import com.afterroot.watchdone.viewmodel.NetworkViewModel
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.jetbrains.anko.browse
import org.jetbrains.anko.design.indefiniteSnackbar
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.startActivity
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import com.afterroot.watchdone.resources.R as CommonR

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val manifestPermissions by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.INTERNET, Manifest.permission.POST_NOTIFICATIONS)
        } else {
            arrayOf(Manifest.permission.INTERNET)
        }
    }

    @Inject lateinit var settings: Settings

    @Inject lateinit var firebaseUtils: FirebaseUtils

    @Inject lateinit var configRepository: ConfigRepository

    @Inject lateinit var firestore: FirebaseFirestore

    @Inject lateinit var firebaseMessaging: FirebaseMessaging

    @Inject
    @Named("feedback_body")
    lateinit var feedbackBody: String
    private val networkViewModel: NetworkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Theme(context = this, settings = settings) {
                Home(onWatchProviderClick = { link ->
                    browse(link, true)
                }, settingsAction = {
                        startActivity<SettingsActivity>()
                    })
            }
        }
        /*binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)

        (this as ComponentActivity).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_common, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.send_feedback -> {
                        email(
                            email = "afterhasroot@gmail.com",
                            subject = "Watchdone Feedback",
                            text = feedbackBody
                        )
                        true
                    }
                    else -> menuItem.onNavDestinationSelected(navController)
                }
            }
        })*/
    }

    override fun onStart() {
        super.onStart()
        if (!firebaseUtils.isUserSignedIn) { // If not logged in, go to login.
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        } else {
            initialize()
        }
        firebaseUtils.auth.addAuthStateListener {
            if (!firebaseUtils.isUserSignedIn) { // If not logged in, go to login.
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
                settings.baseUrl = configRepository.getConfig().imagesConfig?.secureBaseUrl
            }
        }
        if (settings.posterSizes == null) {
            lifecycleScope.launch {
                val set = mutableSetOf<String>()
                try {
                    configRepository.getConfig().imagesConfig?.posterSizes?.map {
                        set.add(it)
                    }
                } catch (_: Exception) {
                } finally {
                    settings.posterSizes = set
                }
            }
        }

        // TODO Use Dialog from Settings
        if (settings.country == null) {
            val country = ConfigurationCompat.getLocales(resources.configuration).get(0)?.country
            settings.country = country
        }

        // Initialize AdMob SDK
        MobileAds.initialize(this) {
        }

        onVersionGreaterThanEqualTo(Build.VERSION_CODES.M, ::checkPermissions)

        // setUpNavigation()

        // Add user in db if not available
        addUserInfoInDB()
        setUpNetworkObserver()
    }

    private var dialog: AlertDialog? = null
    private fun setUpNetworkObserver() {
        networkViewModel.monitor(
            this,
            onConnect = {
                if (dialog != null && dialog?.isShowing!!) dialog?.dismiss()
            },
            onDisconnect = {
                dialog = showNetworkDialog(
                    state = it,
                    positive = { dialog?.dismiss() },
                    negative = { finish() },
                    isShowHide = true
                )
            }
        )
    }

    /**
     * Add user info in FireStore Database
     */
    private fun addUserInfoInDB() {
        try {
            val curUser = firebaseUtils.firebaseUser
            val userRef = firestore.collection(Collection.USERS).document(curUser!!.uid)
            firebaseMessaging.token
                .addOnCompleteListener(
                    OnCompleteListener { tokenTask ->
                        if (!tokenTask.isSuccessful) {
                            return@OnCompleteListener
                        }
                        userRef.get().addOnCompleteListener { getUserTask ->
                            if (getUserTask.isSuccessful) {
                                if (!getUserTask.result.exists()) {
                                    binding.container.snackbar("User not available. Creating User..").anchorView =
                                        binding.toolbar
                                    val user = LocalUser(
                                        name = curUser.displayName,
                                        email = curUser.email,
                                        uid = curUser.uid,
                                        fcmId = tokenTask.result
                                    )
                                    userRef.set(user).addOnCompleteListener { setUserTask ->
                                        if (!setUserTask.isSuccessful) {
                                            Timber.e(
                                                setUserTask.exception,
                                                "Can't create firebaseUser"
                                            )
                                        }
                                    }
                                } else if (getUserTask.result[Field.FCM_ID] != tokenTask.result) {
                                    userRef.update(Field.FCM_ID, tokenTask.result)
                                }
                            } else {
                                Timber.e(getUserTask.exception, "Unknown Error")
                            }
                        }
                    }
                )
        } catch (e: Exception) {
            Timber.e("addUserInfoInDB: $e")
        }
    }

    private fun checkPermissions() {
        val permissionChecker = PermissionChecker(this)
        if (permissionChecker.lacksPermissions(manifestPermissions)) { // missing permissions
            ActivityCompat.requestPermissions(this, manifestPermissions, RC_PERMISSION)
        } else { // no missing permissions
            // setUpNavigation()
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
                        getString(CommonR.string.msg_grant_app_permissions),
                        getString(CommonR.string.text_action_grant)
                    ) {
                        checkPermissions()
                    }.anchorView = binding.toolbar
                } else {
                    // setUpNavigation()
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
                    setTitle(null)
                    binding.fab.apply {
                        show()
                        setOnClickListener { navController.navigate(R.id.toSearchNew) }
                        setImageDrawable(context.getDrawableExt(CommonR.drawable.ic_search))
                    }
                    drawerToggle.apply {
                        if (progress == 1f) progress(1f, 0f) // As hamburger
                    }
                    binding.toolbar.setNavigationOnClickListener {
                        BottomNavDrawerFragment().apply {
                            show(supportFragmentManager, this.tag)
                        }
                    }
                }

                R.id.navigation_settings -> {
                    setTitle(getString(CommonR.string.title_settings))
                    binding.fab.hide()
                    drawerToggle.progress(0f, 1f) // As back arrow
                }

                R.id.navigation_edit_profile -> {
                    setTitle(getString(CommonR.string.title_edit_profile))
                    binding.fab.show()
                    drawerToggle.progress(0f, 1f) // As back arrow
                }

                R.id.navigation_discover -> {
                    binding.titleLayout.visible(false, AutoTransition())
                    setTitle(null)
                    binding.fab.hide()
                    drawerToggle.progress(0f, 1f) // As back arrow
                }

                R.id.navigation_search_new -> {
                    binding.titleLayout.visible(false, AutoTransition())
                    setTitle(null)
                    binding.fab.hide()
                    drawerToggle.progress(0f, 1f) // As back arrow
                }

                R.id.navigation_media_info -> {
                    setTitle(null)
                    binding.titleLayout.visible(false)
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

    /*
        override fun onSupportNavigateUp(): Boolean {
            return navController.navigateUp()
        }
    */
}
