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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.os.ConfigurationCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.afterroot.data.utils.FirebaseUtils
import com.afterroot.tmdbapi.repository.ConfigRepository
import com.afterroot.ui.common.compose.theme.Theme
import com.afterroot.utils.onVersionGreaterThanEqualTo
import com.afterroot.watchdone.base.Collection
import com.afterroot.watchdone.base.Constants.RC_PERMISSION
import com.afterroot.watchdone.base.Field
import com.afterroot.watchdone.data.model.LocalUser
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.common.showNetworkDialog
import com.afterroot.watchdone.ui.home.Home
import com.afterroot.watchdone.ui.settings.SettingsActivity
import com.afterroot.watchdone.utils.PermissionChecker
import com.afterroot.watchdone.utils.logFirstStart
import com.afterroot.watchdone.utils.shareToInstagram
import com.afterroot.watchdone.viewmodel.NetworkViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.launch
import org.jetbrains.anko.browse
import org.jetbrains.anko.startActivity
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

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
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = !isSystemInDarkTheme()

            DisposableEffect(systemUiController, useDarkIcons) {
                // Update all of the system bar colors to be transparent, and use
                // dark icons if we're in light theme
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = useDarkIcons,
                )

                onDispose {}
            }

            Theme(context = this, settings = settings) {
                Home(
                    onWatchProviderClick = { link ->
                        browse(link, true)
                    },
                    settingsAction = {
                        startActivity<SettingsActivity>()
                    },
                    shareToIG = { mediaId, poster ->
                        lifecycleScope.launch {
                            shareToInstagram(poster, mediaId, settings)
                        }
                    },
                )
            }
        }
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
            logFirstStart()
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
                    isShowHide = true,
                )
            },
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
                                    // binding.container.snackbar("User not available. Creating User..").anchorView = binding.toolbar
                                    val user = LocalUser(
                                        name = curUser.displayName,
                                        email = curUser.email,
                                        uid = curUser.uid,
                                        fcmId = tokenTask.result,
                                    )
                                    userRef.set(user).addOnCompleteListener { setUserTask ->
                                        if (!setUserTask.isSuccessful) {
                                            Timber.e(
                                                setUserTask.exception,
                                                "Can't create firebaseUser",
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
                    },
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
                    // TODO
                    /*binding.container.indefiniteSnackbar(
                        getString(CommonR.string.msg_grant_app_permissions),
                        getString(CommonR.string.text_action_grant)
                    ) {
                        checkPermissions()
                    }.anchorView = binding.toolbar*/
                } else {
                    // setUpNavigation()
                }
            }
        }
    }
}
