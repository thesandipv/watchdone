/*
 * Copyright (C) 2020 Sandip Vaghela
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
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.afterroot.core.extensions.getDrawableExt
import com.afterroot.core.extensions.progress
import com.afterroot.tmdbapi.TmdbApi
import com.afterroot.watchdone.BuildConfig
import com.afterroot.watchdone.Constants.RC_PERMISSION
import com.afterroot.watchdone.R
import com.afterroot.watchdone.database.model.Collection
import com.afterroot.watchdone.database.model.Field
import com.afterroot.watchdone.database.model.User
import com.afterroot.watchdone.ui.settings.Settings
import com.afterroot.watchdone.utils.FirebaseUtils
import com.afterroot.watchdone.utils.PermissionChecker
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.design.indefiniteSnackbar
import org.jetbrains.anko.design.snackbar
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val manifestPermissions = arrayOf(Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val settings: Settings by inject()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }

    override fun onStart() {
        super.onStart()
        if (get<FirebaseAuth>().currentUser == null) { //If not logged in, go to login.
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        } else initialize()
        get<FirebaseAuth>().addAuthStateListener {
            if (get<FirebaseAuth>().currentUser == null) { //If not logged in, go to login.
                startActivity(Intent(applicationContext, SplashActivity::class.java))
                finish()
            }
        }
    }

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
            settings.baseUrl = get<TmdbApi>().configuration.secureBaseUrl
        }
        if (settings.posterSizes == null) {
            val set = mutableSetOf<String>()
            try {
                get<TmdbApi>().configuration.posterSizes.map {
                    set.add(it)
                }
            } catch (e: Exception) {
            } finally {
                settings.posterSizes = set
            }
        }
        //Initialize AdMob SDK
        MobileAds.initialize(this) {
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Greater than Lollipop
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    checkPermissions() //Load Fragments after checking permissions
                }
            }
        } else {
            loadFragments() //Less than Lollipop, direct load fragments
        }

        //Add user in db if not available
        addUserInfoInDB()
    }

    /**
     * Add user info in FireStore Database
     */
    private fun addUserInfoInDB() {
        try {
            val curUser = FirebaseUtils.auth!!.currentUser
            val userRef = get<FirebaseFirestore>().collection(Collection.USERS).document(curUser!!.uid)
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { tokenTask ->
                    if (!tokenTask.isSuccessful) {
                        return@OnCompleteListener
                    }
                    userRef.get().addOnCompleteListener { getUserTask ->
                        if (getUserTask.isSuccessful) {
                            if (!getUserTask.result!!.exists()) {
                                container.snackbar("User not available. Creating User..").anchorView = toolbar
                                val user = User(curUser.displayName, curUser.email, curUser.uid, tokenTask.result?.token!!)
                                userRef.set(user).addOnCompleteListener { setUserTask ->
                                    if (!setUserTask.isSuccessful) Log.e(
                                        TAG,
                                        "Can't create firebaseUser",
                                        setUserTask.exception
                                    )
                                }
                            } else if (getUserTask.result!![Field.FCM_ID] != tokenTask.result?.token!!) {
                                userRef.update(Field.FCM_ID, tokenTask.result?.token!!)
                            }
                        } else Log.e(TAG, "Unknown Error", getUserTask.exception)
                    }
                })
        } catch (e: Exception) {
            Log.e(TAG, "addUserInfoInDB: $e")
        }
    }

    private fun checkPermissions() {
        val permissionChecker = PermissionChecker(this)
        if (permissionChecker.lacksPermissions(manifestPermissions)) { //missing permissions
            ActivityCompat.requestPermissions(this, manifestPermissions, RC_PERMISSION)
        } else { //no missing permissions
            loadFragments()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            RC_PERMISSION -> {
                val isPermissionNotGranted =
                    grantResults.isNotEmpty() && grantResults.any { it == PackageManager.PERMISSION_DENIED }
                if (isPermissionNotGranted) {
                    container.indefiniteSnackbar(
                        getString(R.string.msg_grant_app_permissions),
                        getString(R.string.text_action_grant)
                    ) {
                        checkPermissions()
                    }.anchorView = toolbar
                } else {
                    loadFragments()
                }
            }
        }
    }

    private fun loadFragments() {
        val drawerToggle = DrawerArrowDrawable(this)
        navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            toolbar.apply {
                performShow()
                hideOnScroll = true
                navigationIcon = drawerToggle
                setNavigationOnClickListener {
                    navController.navigateUp()
                }
            }
            when (destination.id) {
                R.id.navigation_home -> {
                    fab.apply {
                        show()
                        setOnClickListener { navController.navigate(R.id.toSearch) }
                        setImageDrawable(context.getDrawableExt(R.drawable.ic_add))
                    }
                    drawerToggle.apply {
                        if (progress == 1f) progress(1f, 0f) //As hamburger
                    }
                    toolbar.apply {
                        setNavigationOnClickListener {
                            BottomNavDrawerFragment().apply {
                                show(supportFragmentManager, this.tag)
                            }
                        }
                        fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
                    }
                }

                R.id.navigation_settings -> {
                    fab.hide()
                    drawerToggle.progress(0f, 1f) //As back arrow
                }
                R.id.navigation_search -> {
                    fab.hide()
                    toolbar.hideOnScroll = false
                    drawerToggle.progress(0f, 1f) //As back arrow
                }
                R.id.navigation_edit_profile -> {
                    fab.show()
                    drawerToggle.progress(0f, 1f) //As back arrow
                }
                R.id.navigation_movie_info -> {
                    fab.hide()
                    drawerToggle.progress(0f, 1f) //As back arrow
                }
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
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
