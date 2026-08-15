/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone

import android.app.Application
import androidx.annotation.Keep
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.crossfade
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp

@Keep
@HiltAndroidApp
class App :
  Application(),
  SingletonImageLoader.Factory {

  override fun onCreate() {
    DynamicColors.applyToActivitiesIfAvailable(this)
    super.onCreate()
  }

  override fun newImageLoader(context: PlatformContext): ImageLoader =
    ImageLoader(context).newBuilder().crossfade(true).build()
}
