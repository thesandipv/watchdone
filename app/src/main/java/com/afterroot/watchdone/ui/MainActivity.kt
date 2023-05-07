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
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import com.afterroot.data.utils.FirebaseUtils
import com.afterroot.tmdbapi.repository.ConfigRepository
import com.afterroot.ui.common.compose.theme.Theme
import com.afterroot.utils.onVersionGreaterThanEqualTo
import com.afterroot.watchdone.BuildConfig
import com.afterroot.watchdone.base.Collection
import com.afterroot.watchdone.base.Constants
import com.afterroot.watchdone.base.Constants.RC_PERMISSION
import com.afterroot.watchdone.base.Field
import com.afterroot.watchdone.data.model.LocalUser
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.common.showNetworkDialog
import com.afterroot.watchdone.ui.home.Home
import com.afterroot.watchdone.ui.settings.SettingsActivity
import com.afterroot.watchdone.utils.PermissionChecker
import com.afterroot.watchdone.viewmodel.NetworkViewModel
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import org.jetbrains.anko.browse
import org.jetbrains.anko.startActivity
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Named

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
        super.onCreate(savedInstanceState)

        setContent {
            Theme(context = this, settings = settings) {
                Home(onWatchProviderClick = { link ->
                    browse(link, true)
                }, settingsAction = {
                        startActivity<SettingsActivity>()
                    }, shareToIG = { mediaId, poster ->
                        shareToInstagram(poster, mediaId)
                    })
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
                                    // binding.container.snackbar("User not available. Creating User..").anchorView = binding.toolbar
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

    private fun shareToInstagram(poster: String, mediaId: Int) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val loader = ImageLoader(this@MainActivity)
                val request = ImageRequest.Builder(this@MainActivity)
                    .data(settings.baseUrl + Constants.IG_SHARE_IMAGE_SIZE + poster)
                    .allowHardware(false)
                    .build()
                val result = loader.execute(request).drawable
                val resource = (result as BitmapDrawable).bitmap

                val fos: FileOutputStream?
                val file = File(
                    this@MainActivity.cacheDir.toString(),
                    "$mediaId.jpg"
                )
                fos = FileOutputStream(file)
                resource.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                fos.close()

                Palette.from(resource).generate { palette ->
                    val map = mapOf(
                        "contentUrl" to HttpUrl.Builder().scheme(Constants.SCHEME_HTTPS).host(Constants.WATCHDONE_HOST)
                            .addPathSegment("movie").addPathSegment(mediaId.toString())
                            .build().toString(),
                        "topBackgroundColor" to palette?.getVibrantColor(
                            palette.getMutedColor(
                                ContextCompat.getColor(
                                    this@MainActivity,
                                    com.afterroot.watchdone.resources.R.color.md_theme_dark_primary
                                )
                            )
                        )?.toHex(),
                        "bottomBackgroundColor" to palette?.getDarkVibrantColor(
                            palette.getDarkMutedColor(
                                ContextCompat.getColor(
                                    this@MainActivity,
                                    com.afterroot.watchdone.resources.R.color.md_theme_light_primaryContainer
                                )
                            )
                        )?.toHex(),
                        "backgroundAssetName" to "$mediaId.jpg"
                    )

                    val intent = createInstagramShareIntent(this@MainActivity, map)

                    if (intent.isResolvable()) {
                        Timber.d("shareToInstagram: Launching IG")
                        startActivity(intent)
                    } else {
                        Timber.d("shareToInstagram: No Activity Resolved")
                        try {
                            startActivity(intent)
                        } catch (e: Exception) {
                            Timber.e(e, "shareToInstagram: Error while sharing")
                            val shareIntent = createShareIntent(this@MainActivity, mapOf("mediaId" to mediaId.toString()))
                            startActivity(Intent.createChooser(shareIntent, "Share to"))
                        }
                    }
                }
            }
        }
    }

    private fun createInstagramShareIntent(
        context: Context,
        intentExtras: Map<String, String?>
    ): Intent {
        val shareIntent = Intent(Constants.IG_SHARE_ACTION)

        val backgroundAssetName = intentExtras["backgroundAssetName"]
        val stickerAssetName = intentExtras["stickerAssetName"]

        if (backgroundAssetName == null && stickerAssetName == null) {
            val exceptionMessage = "Background and Sticker asset should not be null"
            val exception = IllegalArgumentException(exceptionMessage)
            throw exception
        }

        backgroundAssetName?.let {
            val backgroundAssetUri =
                FileProvider.getUriForFile(context, Constants.IG_SHARE_PROVIDER, File(context.cacheDir, it))
            Timber.d("createInstagramShareIntent: URI $backgroundAssetUri")
            shareIntent.apply {
                type = Constants.MIME_TYPE_JPEG
                putExtra("interactive_asset_uri", backgroundAssetUri)
                putExtra(Intent.EXTRA_STREAM, backgroundAssetUri)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            context.grantUriPermission(Constants.IG_PACKAGE_NAME, backgroundAssetUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        shareIntent.apply {
            intentExtras["topBackgroundColor"]?.let { putExtra(Constants.IG_EXTRA_TOP_COLOR, it) }
            intentExtras["bottomBackgroundColor"]?.let { putExtra(Constants.IG_EXTRA_BOTTOM_COLOR, it) }
            intentExtras["contentUrl"]?.let { putExtra(Constants.IG_EXTRA_CONTENT_URL, it) }
            putExtra(Constants.IG_EXTRA_SOURCE_APP, com.afterroot.watchdone.media.BuildConfig.FB_APP_ID)
        }

        return shareIntent
    }

    fun Intent.isResolvable(flags: Long = 0): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this@MainActivity.packageManager.resolveActivity(
                this,
                PackageManager.ResolveInfoFlags.of(flags)
            ) != null
        } else {
            @Suppress("DEPRECATION")
            this@MainActivity.packageManager.resolveActivity(this, flags.toInt()) != null
        }
    }

    private fun Int.toHex() = "#${Integer.toHexString(this)}"

    private fun createShareIntent(
        context: Context,
        intentExtras: Map<String, String?>
    ): Intent {
        val shareIntent = Intent(Intent.ACTION_SEND)

        val mediaId = intentExtras["mediaId"]

        if (mediaId == null) {
            val exceptionMessage = "MediaId should not be null"
            val exception = IllegalArgumentException(exceptionMessage)
            throw exception
        }

        val uri = FileProvider.getUriForFile(context, Constants.IG_SHARE_PROVIDER, File(context.cacheDir, "$mediaId.jpg"))
        shareIntent.apply {
            type = Constants.MIME_TYPE_JPEG
            putExtra(Intent.EXTRA_STREAM, uri)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        return shareIntent
    }
}
