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

package com.afterroot.watchdone.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.FileProvider
import com.afterroot.watchdone.base.Constants
import java.io.File
import timber.log.Timber

private fun createInstagramShareIntent(
  context: Context,
  intentExtras: Map<String, String?>,
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
      FileProvider.getUriForFile(
        context,
        Constants.IG_SHARE_PROVIDER,
        File(context.cacheDir, it),
      )
    Timber.d("createInstagramShareIntent: URI $backgroundAssetUri")
    shareIntent.apply {
      type = Constants.MIME_TYPE_JPEG
      putExtra("interactive_asset_uri", backgroundAssetUri)
      putExtra(Intent.EXTRA_STREAM, backgroundAssetUri)
      flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    context.grantUriPermission(
      Constants.IG_PACKAGE_NAME,
      backgroundAssetUri,
      Intent.FLAG_GRANT_READ_URI_PERMISSION,
    )
  }

  shareIntent.apply {
    intentExtras["topBackgroundColor"]?.let { putExtra(Constants.IG_EXTRA_TOP_COLOR, it) }
    intentExtras["bottomBackgroundColor"]?.let { putExtra(Constants.IG_EXTRA_BOTTOM_COLOR, it) }
    intentExtras["contentUrl"]?.let { putExtra(Constants.IG_EXTRA_CONTENT_URL, it) }
    putExtra(Constants.IG_EXTRA_SOURCE_APP, com.afterroot.watchdone.base.BuildConfig.FB_APP_ID)
  }

  return shareIntent
}

fun Intent.isResolvable(context: Context, flags: Long = 0): Boolean {
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    context.packageManager.resolveActivity(
      this,
      PackageManager.ResolveInfoFlags.of(flags),
    ) != null
  } else {
    @Suppress("DEPRECATION")
    context.packageManager.resolveActivity(this, flags.toInt()) != null
  }
}

fun Int.toHex() = "#${Integer.toHexString(this)}"

private fun createShareIntent(
  context: Context,
  intentExtras: Map<String, String?>,
): Intent {
  val shareIntent = Intent(Intent.ACTION_SEND)

  val mediaId = intentExtras["mediaId"]

  if (mediaId == null) {
    val exceptionMessage = "MediaId should not be null"
    val exception = IllegalArgumentException(exceptionMessage)
    throw exception
  }

  val uri = FileProvider.getUriForFile(
    context,
    Constants.IG_SHARE_PROVIDER,
    File(context.cacheDir, "$mediaId.jpg"),
  )
  shareIntent.apply {
    type = Constants.MIME_TYPE_JPEG
    putExtra(Intent.EXTRA_STREAM, uri)
    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
  }

  return shareIntent
}

fun Context.createExternalShareIntent(intentExtras: Map<String, String?>): Intent {
  val instagramIntent = createInstagramShareIntent(this, intentExtras)

  val shareIntent = createShareIntent(this, intentExtras)
  val chooserIntent = Intent.createChooser(shareIntent, "Share")
  chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(instagramIntent))
  return chooserIntent
}
