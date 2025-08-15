/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import com.afterroot.watchdone.BuildConfig
import com.afterroot.watchdone.base.Constants
import com.afterroot.watchdone.resources.R
import com.afterroot.watchdone.settings.Settings
import com.google.firebase.analytics.FirebaseAnalytics
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import timber.log.Timber

suspend fun Context.shareToInstagram(poster: String, mediaId: Int, settings: Settings) {
  withContext(Dispatchers.IO) {
    val file = File(
      this@shareToInstagram.cacheDir.toString(),
      "$mediaId.jpg",
    )
    if (!file.exists()) {
      val loader = ImageLoader(this@shareToInstagram)
      val request = ImageRequest.Builder(this@shareToInstagram)
        .data(settings.baseUrl + Constants.IG_SHARE_IMAGE_SIZE + poster)
        .allowHardware(false)
        .build()
      val result = loader.execute(request).drawable
      val resource = (result as BitmapDrawable).bitmap

      val fos: FileOutputStream?

      fos = FileOutputStream(file)
      resource.compress(Bitmap.CompressFormat.JPEG, 100, fos)
      fos.flush()
      fos.close()
    }

    val resource = BitmapFactory.decodeFile("${this@shareToInstagram.cacheDir}/$mediaId.jpg")

    Palette.from(resource).generate { palette ->
      val map = mapOf(
        "contentUrl" to
          HttpUrl.Builder().scheme(Constants.SCHEME_HTTPS).host(Constants.WATCHDONE_HOST)
            .addPathSegment("movie").addPathSegment(mediaId.toString())
            .build().toString(),
        "topBackgroundColor" to palette?.getVibrantColor(
          palette.getMutedColor(
            ContextCompat.getColor(
              this@shareToInstagram,
              R.color.md_theme_dark_primary,
            ),
          ),
        )?.toHex(),
        "bottomBackgroundColor" to palette?.getDarkVibrantColor(
          palette.getDarkMutedColor(
            ContextCompat.getColor(
              this@shareToInstagram,
              R.color.md_theme_light_primaryContainer,
            ),
          ),
        )?.toHex(),
        "backgroundAssetName" to "$mediaId.jpg",
        "mediaId" to mediaId.toString(),
      )

      try {
        startActivity(createExternalShareIntent(map))
      } catch (e: Exception) {
        Timber.e(e, "shareToInstagram: Error while sharing")
      }
    }
  }
}

fun Context.logFirstStart() {
  FirebaseAnalytics.getInstance(this@logFirstStart).logEvent(
    "DeviceInfo",
    bundleOf(
      "Device_Name" to Build.DEVICE,
      "Device_Model" to Build.MODEL,
      "Manufacturer" to Build.MANUFACTURER,
      "AndroidVersion" to Build.VERSION.RELEASE,
      "AppVersion" to BuildConfig.VERSION_CODE.toString(),
      "Package" to BuildConfig.APPLICATION_ID,
    ),
  )
}
