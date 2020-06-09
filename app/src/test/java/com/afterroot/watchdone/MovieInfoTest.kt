/*
 * Copyright (C) 2020 Sandip Vaghela
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

import com.afterroot.tmdbapi2.repository.MoviesRepository
import com.afterroot.watchdone.di.apiModule
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

class MovieInfoTest : KoinTest {
    @get:Rule
    val testRule = KoinTestRule.create {
        printLogger()
        modules(apiModule)
    }

    private val moviesRepository by inject<MoviesRepository>()

    @Test
    fun `MovieDb Working`() {
        runBlocking {
            launch {
                Assert.assertEquals("Fight Club", moviesRepository.getMovieInfo(550).title)
            }
        }
    }
}