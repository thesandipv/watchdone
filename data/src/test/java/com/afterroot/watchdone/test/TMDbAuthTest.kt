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
package com.afterroot.watchdone.test

import com.afterroot.tmdbapi2.model.RequestBodyToken
import com.afterroot.tmdbapi2.repository.AuthRepository
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class TMDbAuthTest : DataTest() {

    @Inject lateinit var authRepository: AuthRepository

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
