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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.afterroot.core.extensions.animateProperty
import com.afterroot.core.extensions.visible
import com.afterroot.tmdbapi.TmdbApi
import com.afterroot.watchdone.BuildConfig
import com.afterroot.watchdone.Constants.RC_PERMISSION
import com.afterroot.watchdone.R
import com.afterroot.watchdone.Settings
import com.afterroot.watchdone.database.DatabaseFields
import com.afterroot.watchdone.model.User
import com.afterroot.watchdone.utils.FirebaseUtils
import com.afterroot.watchdone.utils.PermissionChecker
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.tasks.OnCompleteListener
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

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val manifestPermissions = arrayOf(Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val settings: Settings by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_settings)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        nav_view.setupWithNavController(navController)
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser == null) { //If not logged in, go to login.
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        } else initialize()
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
            settings.baseUrl = get<TmdbApi>().configuration?.secureBaseUrl
        }
        if (settings.posterSizes == null) {
            val set = mutableSetOf<String>()
            try {
                get<TmdbApi>().configuration?.posterSizes?.map {
                    set.add(it)
                }
            } catch (e: Exception) {
            } finally {
                settings.posterSizes = set
            }
        }
        //Initialize AdMob SDK
        MobileAds.initialize(this, getString(R.string.admob_app_id))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Greater than Lollipop
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    checkPermissions()
                }
            }
        } else {
            loadFragments() //Less than Lollipop, direct load fragments
        }

        //Add user in db if not available
        addUserInfoInDB()
    }

    private fun addUserInfoInDB() {
        try {
            val curUser = FirebaseUtils.auth!!.currentUser
            val userRef = get<FirebaseFirestore>().collection(DatabaseFields.COLLECTION_USERS).document(curUser!!.uid)
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { tokenTask ->
                    if (!tokenTask.isSuccessful) {
                        return@OnCompleteListener
                    }
                    userRef.get().addOnCompleteListener { getUserTask ->
                        if (getUserTask.isSuccessful) {
                            if (!getUserTask.result!!.exists()) {
                                container.snackbar("User not available. Creating User..").anchorView = nav_view
                                val user = User(curUser.displayName, curUser.email, curUser.uid, tokenTask.result?.token!!)
                                userRef.set(user).addOnCompleteListener { setUserTask ->
                                    if (!setUserTask.isSuccessful) Log.e(
                                        TAG,
                                        "Can't create firebaseUser",
                                        setUserTask.exception
                                    )
                                }
                            } else if (getUserTask.result!![DatabaseFields.FIELD_FCM_ID] != tokenTask.result?.token!!) {
                                userRef.update(DatabaseFields.FIELD_FCM_ID, tokenTask.result?.token!!)
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
        if (permissionChecker.lacksPermissions(manifestPermissions)) {
            ActivityCompat.requestPermissions(this, manifestPermissions, RC_PERMISSION)
        } else {
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
                    }.anchorView = nav_view
                } else {
                    loadFragments()
                }
            }
        }
    }

    private fun hideNavigation() {
        if (nav_view.isVisible) {
            nav_view.run {
                animateProperty("translationY", 0f, nav_view.height.toFloat(), 200)
                visible(false)
            }
        }
    }

    private fun showNavigation() {
        if (!nav_view.isVisible) {
            nav_view.run {
                animateProperty("translationY", nav_view.height.toFloat(), 0f, 200)
                visible(true)
            }
        }
    }

    private fun loadFragments() {
        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener { _, destination, _ ->
            hideNavigation()
            when (destination.id) {
                R.id.navigation_home -> {
                    //TODO
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
