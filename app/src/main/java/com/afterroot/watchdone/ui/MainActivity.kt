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
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.os.ConfigurationCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import app.tivi.util.Logger
import com.afterroot.data.utils.FirebaseUtils
import com.afterroot.tmdbapi.repository.ConfigRepository
import com.afterroot.ui.common.compose.components.LocalLogger
import com.afterroot.ui.common.compose.components.LocalWindowSizeClass
import com.afterroot.ui.common.compose.theme.Theme
import com.afterroot.ui.common.compose.utils.darkScrim
import com.afterroot.ui.common.compose.utils.lightScrim
import com.afterroot.ui.common.compose.utils.shouldUseDarkTheme
import com.afterroot.utils.onVersionGreaterThanEqualTo
import com.afterroot.watchdone.base.Collection
import com.afterroot.watchdone.base.Constants.RC_PERMISSION
import com.afterroot.watchdone.base.Field
import com.afterroot.watchdone.data.model.LocalUser
import com.afterroot.watchdone.data.model.UserData
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.common.showNetworkDialog
import com.afterroot.watchdone.ui.home.Home
import com.afterroot.watchdone.ui.settings.SettingsActivity
import com.afterroot.watchdone.utils.PermissionChecker
import com.afterroot.watchdone.utils.State
import com.afterroot.watchdone.utils.logFirstStart
import com.afterroot.watchdone.utils.shareToInstagram
import com.afterroot.watchdone.viewmodel.MainActivityViewModel
import com.afterroot.watchdone.viewmodel.NetworkViewModel
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.anko.browse
import org.jetbrains.anko.startActivity
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

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

  @Inject lateinit var logger: Logger

  @Inject
  @Named("feedback_body")
  lateinit var feedbackBody: String
  private val networkViewModel: NetworkViewModel by viewModels()
  private val mainActivityViewModel: MainActivityViewModel by viewModels()

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    val splashScreen = installSplashScreen()
    super.onCreate(savedInstanceState)

    var uiState: State<UserData> by mutableStateOf(State.loading())

    // Update the uiState
    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        mainActivityViewModel.uiState
          .onEach { uiState = it }
          .collect()
      }
    }

    // Keep the splash screen on-screen until the UI state is loaded. This condition is
    // evaluated each time the app needs to be redrawn so it should be fast to avoid blocking
    // the UI.
    splashScreen.setKeepOnScreenCondition {
      when (uiState) {
        is State.Loading -> true
        is State.Failed -> false
        is State.Success -> false
      }
    }

    enableEdgeToEdge()
    setContent {
      val darkTheme = shouldUseDarkTheme(uiState)

      DisposableEffect(darkTheme) {
        enableEdgeToEdge(
          statusBarStyle = SystemBarStyle.auto(
            android.graphics.Color.TRANSPARENT,
            android.graphics.Color.TRANSPARENT,
          ) { darkTheme },
          navigationBarStyle = SystemBarStyle.auto(
            lightScrim,
            darkScrim,
          ) { darkTheme },
        )
        onDispose {}
      }

      CompositionLocalProvider(
        LocalLogger provides logger,
        LocalWindowSizeClass provides calculateWindowSizeClass(this),
      ) {
        Theme(settings = settings, darkTheme = darkTheme) {
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
  }

  override fun onStart() {
    super.onStart()
    if (!firebaseUtils.isUserSignedIn) { // If not logged in, go to login.
      startActivity(Intent(this, OnboardingActivity::class.java))
      finish()
    } else {
      initialize()
    }
    firebaseUtils.auth.addAuthStateListener {
      if (!firebaseUtils.isUserSignedIn) { // If not logged in, go to login.
        startActivity(Intent(applicationContext, OnboardingActivity::class.java))
        finish()
      }
    }
  }

  @SuppressLint("MissingPermission")
  private fun initialize() {
    lifecycleScope.launch {
      mainActivityViewModel.checkForMigrations()
    }
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
      // TODO Move to viewmodel
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
    MobileAds.initialize(this)

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
   * TODO Move to viewmodel
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

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray,
  ) {
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
