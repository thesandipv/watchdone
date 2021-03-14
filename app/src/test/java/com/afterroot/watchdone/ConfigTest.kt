/*
 * Copyright (C) 2020-2021 Sandip Vaghela
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

package com.afterroot.watchdone

import com.afterroot.tmdbapi2.repository.ConfigRepository
import com.afterroot.watchdone.di.apiModule
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.koin.core.logger.Level
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

class ConfigTest : KoinTest {
    @get:Rule
    val testRule = KoinTestRule.create {
        printLogger(Level.ERROR)
        modules(apiModule)
    }

    private val configRepository: ConfigRepository by inject()

    @Test
    fun `Config Result`() {
        runBlocking {
            launch {
                assertNotNull(configRepository.getConfig())
            }
        }
    }
}