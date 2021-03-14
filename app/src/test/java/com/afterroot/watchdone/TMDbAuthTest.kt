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

import com.afterroot.tmdbapi2.model.RequestBodyToken
import com.afterroot.tmdbapi2.repository.AuthRepository
import com.afterroot.watchdone.di.apiModule
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.koin.core.logger.Level
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

class TMDbAuthTest : KoinTest {
    @get:Rule
    val testRule = KoinTestRule.create {
        printLogger(Level.ERROR)
        modules(apiModule)
    }

    private val authRepository by inject<AuthRepository>()

    @Test
    fun `Verify request token valid`() {
        launch {
            val token =
                authRepository.createRequestToken(RequestBodyToken("https://afterroot.web.app/apps/watchdone/launch")).requestToken
            Assert.assertNotNull("Token is Null", token)

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