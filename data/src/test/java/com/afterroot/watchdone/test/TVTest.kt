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

import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.data.repositories.TVRepository
import com.afterroot.watchdone.data.search.SearchDataSource
import com.afterroot.watchdone.data.search.SearchRepository
import com.afterroot.watchdone.utils.State
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

@HiltAndroidTest
class TVTest : DataTest() {

  @Inject lateinit var tvRepository: TVRepository

  @Inject lateinit var searchRepository: SearchRepository

  @Test
  fun `TV Working`() {
    launch {
      val name = tvRepository.info(1399).first { it is State.Success }.successResult()?.name
      Assert.assertEquals("Game of Thrones", name)
    }
  }

  @Test
  fun `search TV`() {
    launch {
      val result = searchRepository.search(
        SearchDataSource.Params(
          mediaType = MediaType.SHOW,
          query = "Game of Thrones",
        ),
      )
      Assert.assertNotNull(result)
      Assert.assertTrue(result.isNotEmpty())
    }
  }

  @Test
  fun `Get Season Info`() {
    launch {
      val season1 = tvRepository.season(1399, 1).first { it is State.Success }.successResult()
      Assert.assertEquals("Season 1", season1?.name)
    }
  }

  @Test
  fun `Get WatchProviders`() {
    launch {
      val wp = tvRepository.watchProviders(66788)
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
