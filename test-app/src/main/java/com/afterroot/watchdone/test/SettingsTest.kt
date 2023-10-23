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
