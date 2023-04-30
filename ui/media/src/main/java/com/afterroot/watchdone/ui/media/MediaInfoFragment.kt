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

package com.afterroot.watchdone.ui.media

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.findNavController
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import com.afterroot.ui.common.compose.components.LocalPosterSize
import com.afterroot.ui.common.compose.theme.Theme
import com.afterroot.watchdone.base.Constants
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.helpers.Deeplink
import com.afterroot.watchdone.media.BuildConfig
import com.afterroot.watchdone.settings.Settings
import dagger.hilt.android.AndroidEntryPoint
import info.movito.themoviedbapi.model.Multi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import org.jetbrains.anko.browse
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class MediaInfoFragment : Fragment() {

    @Inject lateinit var settings: Settings

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Theme(context = requireContext(), settings = settings) {
                    MediaInfoContent(navigateUp = findNavController()::navigateUp, onRecommendedClick = {
                        if (it is Movie) {
                            val request = NavDeepLinkRequest.Builder
                                .fromUri(Deeplink.media(it.id, it.mediaType))
                                .build()
                            findNavController().navigate(request)
                        } else if (it is TV) {
                            val request = NavDeepLinkRequest.Builder
                                .fromUri(Deeplink.media(it.id, it.mediaType))
                                .build()
                            findNavController().navigate(request)
                        }
                    }, onWatchProviderClick = { link ->
                            requireContext().browse(link, true)
                        }, shareToIG = { mediaId: Int, poster: String ->
                            shareToInstagram(poster, mediaId)
                        })
                }
            }
        }
    }

    @Composable
    fun MediaInfoContent(
        navigateUp: () -> Unit,
        onRecommendedClick: (media: Multi) -> Unit,
        onWatchProviderClick: (link: String) -> Unit = { _ -> },
        shareToIG: ((mediaId: Int, poster: String) -> Unit)? = null
    ) {
        CompositionLocalProvider(
            LocalPosterSize provides (
                this@MediaInfoFragment.settings.imageSize
                    ?: this@MediaInfoFragment.settings.defaultImagesSize
                )
        ) {
            MediaInfo(
                navigateUp = navigateUp,
                onRecommendedClick = onRecommendedClick,
                onWatchProviderClick = onWatchProviderClick,
                shareToIG = shareToIG
            )
        }
    }

    private fun shareToInstagram(poster: String, mediaId: Int) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val loader = ImageLoader(requireContext())
                val request = ImageRequest.Builder(requireContext())
                    .data(settings.baseUrl + Constants.IG_SHARE_IMAGE_SIZE + poster)
                    .allowHardware(false)
                    .build()
                val result = loader.execute(request).drawable
                val resource = (result as BitmapDrawable).bitmap

                val fos: FileOutputStream?
                val file = File(
                    requireContext().cacheDir.toString(),
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
                                    requireContext(),
                                    com.afterroot.watchdone.resources.R.color.md_theme_dark_primary
                                )
                            )
                        )?.toHex(),
                        "bottomBackgroundColor" to palette?.getDarkVibrantColor(
                            palette.getDarkMutedColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    com.afterroot.watchdone.resources.R.color.md_theme_light_primaryContainer
                                )
                            )
                        )?.toHex(),
                        "backgroundAssetName" to "$mediaId.jpg"
                    )

                    val intent = createInstagramShareIntent(requireContext(), requireActivity(), map)

                    if (intent.isResolvable()) {
                        Timber.d("testCoil: Launching IG")
                        requireActivity().startActivity(intent)
                    } else {
                        Timber.d("testCoil: No Activity Resolved")
                        try {
                            requireActivity().startActivity(intent)
                        } catch (e: Exception) {
                            Timber.e(e, "shareToInstagram: Error while sharing")
                        }
                    }
                    // requireActivity().startActivity(Intent.createChooser(intent, "Share to"))
                }
            }
        }
    }

    fun Intent.isResolvable(flags: Long = 0): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().packageManager.resolveActivity(
                this,
                PackageManager.ResolveInfoFlags.of(flags)
            ) != null
        } else {
            @Suppress("DEPRECATION")
            requireActivity().packageManager.resolveActivity(this, flags.toInt()) != null
        }
    }

    private fun createInstagramShareIntent(
        context: Context,
        activity: Activity,
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
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            activity.grantUriPermission(Constants.IG_PACKAGE_NAME, backgroundAssetUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        shareIntent.apply {
            intentExtras["topBackgroundColor"]?.let { putExtra(Constants.IG_EXTRA_TOP_COLOR, it) }
            intentExtras["bottomBackgroundColor"]?.let { putExtra(Constants.IG_EXTRA_BOTTOM_COLOR, it) }
            intentExtras["contentUrl"]?.let { putExtra(Constants.IG_EXTRA_CONTENT_URL, it) }
            putExtra(Constants.IG_EXTRA_SOURCE_APP, BuildConfig.FB_APP_ID)
        }

        return shareIntent
    }

    private fun createShareIntent(
        context: Context,
        activity: Activity,
        intentExtras: Map<String, String?>
    ): Intent {
        val shareIntent = Intent(Intent.ACTION_SEND)

        val mediaId = intentExtras["mediaId"]

        if (mediaId == null) {
            val exceptionMessage = "MediaId should not be null"
            val exception = IllegalArgumentException(exceptionMessage)
            throw exception
        }

        val uri = FileProvider.getUriForFile(context, Constants.IG_SHARE_PROVIDER, File(context.cacheDir, mediaId))
        shareIntent.apply {
            type = Constants.MIME_TYPE_JPEG
            putExtra(Intent.EXTRA_STREAM, uri)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        return shareIntent
    }

    private fun Int.toHex() = "#${Integer.toHexString(this)}"
}
