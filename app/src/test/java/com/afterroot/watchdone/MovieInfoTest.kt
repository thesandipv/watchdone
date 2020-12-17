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

import com.afterroot.tmdbapi.model.ArtworkType
import com.afterroot.tmdbapi2.model.MovieAppendableResponses.images
import com.afterroot.tmdbapi2.model.MovieAppendableResponses.videos
import com.afterroot.tmdbapi2.repository.MoviesRepository
import com.afterroot.tmdbapi2.repository.SearchRepository
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

class MovieInfoTest : KoinTest {
    @get:Rule
    val testRule = KoinTestRule.create {
        printLogger(Level.ERROR)
        modules(apiModule)
    }

    private val moviesRepository by inject<MoviesRepository>()
    private val searchRepository by inject<SearchRepository>()

    @Test
    fun `MovieDb Working`() {
        launch {
            Assert.assertEquals("Fight Club", moviesRepository.getMovieInfo(550).title)
        }
    }

    @Test
    fun `Full Movie Info`() {
        launch {
            val response = moviesRepository.getFullMovieInfo(550, images, videos)
            Assert.assertNotNull("Images is null", response.getImages(ArtworkType.POSTER))
            response.getImages(ArtworkType.POSTER)?.forEach {
                println(it.toString())
            }
            Assert.assertNotNull("Videos is null", response.getVideos())
            response.getVideos()?.forEach {
                println(it.toString())
            }
        }
    }

    @Test
    fun `search Movies`() {
        launch {
            val result = searchRepository.searchMovie("Fight Club")
            Assert.assertNotNull(result)
            Assert.assertNotNull(result.results)
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