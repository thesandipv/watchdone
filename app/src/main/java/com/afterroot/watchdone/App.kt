/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone

import android.app.Application
import androidx.annotation.Keep
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp

@Keep
@HiltAndroidApp
class App :
  Application(),
  ImageLoaderFactory {
  override fun onCreate() {
    DynamicColors.applyToActivitiesIfAvailable(this)
    super.onCreate()
  }

  override fun newImageLoader(): ImageLoader = ImageLoader.Builder(this)
    .crossfade(true)
    .build()
}
