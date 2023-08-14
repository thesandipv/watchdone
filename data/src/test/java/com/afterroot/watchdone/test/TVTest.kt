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

import com.afterroot.tmdbapi.repository.SearchRepository
import com.afterroot.tmdbapi.repository.TVRepository
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

@HiltAndroidTest
class TVTest : DataTest() {

    @Inject lateinit var tvRepository: TVRepository

    @Inject lateinit var tvRepository2: com.afterroot.watchdone.data.repositories.TVRepository

    @Inject lateinit var searchRepository: SearchRepository

    @Test
    fun `TV Working`() {
        launch {
            Assert.assertEquals("Game of Thrones", tvRepository.getTVInfo(1399).name)
        }
    }

    @Test
    fun `search TV`() {
        launch {
            val result = searchRepository.searchTv("Game of Thrones")
            Assert.assertNotNull(result)
            Assert.assertNotNull(result.results)
        }
    }

    @Test
    fun `Get Season Info`() {
        launch {
            val season1 = tvRepository.getSeason(1399, 1)
            Assert.assertEquals("Season 1", season1.name)
        }
    }

    @Test
    fun `Get WatchProviders`() {
        launch {
            val wp = tvRepository2.watchProviders(66788)
            wp.collectLatest {
                it.whenSuccess {
                    println("Get WatchProviders: $wp")
                }
            }
        }
    }

    /*@Test
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
    }*/

    private fun launch(block: suspend () -> Unit) {
        runBlocking {
            launch {
                block()
            }
        }
    }
}
