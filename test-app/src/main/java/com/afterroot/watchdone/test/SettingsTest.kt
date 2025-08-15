/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.test

import com.afterroot.watchdone.core.testing.AppTest
import com.afterroot.watchdone.settings.Settings
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

@HiltAndroidTest
class SettingsTest : AppTest() {
  @Inject lateinit var settings: Settings

  @Test
  fun settingsWorking() {
    launch {
      Assert.assertNotNull(settings)
    }
  }

  private fun launch(block: suspend () -> Unit) {
    runBlocking {
      launch {
        block()
      }
    }
  }
}
