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

import com.afterroot.tmdbapi.model.MovieAppendableResponses
import com.afterroot.tmdbapi.repository.SearchRepository
import com.afterroot.watchdone.data.repositories.MovieRepository
import com.afterroot.watchdone.utils.State
import dagger.hilt.android.testing.HiltAndroidTest
import info.movito.themoviedbapi.model.ArtworkType
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

@HiltAndroidTest
class MovieInfoTest : DataTest() {

  @Inject lateinit var moviesRepository: MovieRepository

  @Inject lateinit var searchRepository: SearchRepository

  @Test
  fun `MovieDb Working`() {
    launch {
      val title = moviesRepository.info(550).first {
        it is State.Success
      }.successResult()?.title
      Assert.assertEquals("Fight Club", title)
    }
  }

  @Test
  fun `Full Movie Info`() {
    launch {
      val response = moviesRepository.info(
        550,
        MovieAppendableResponses.images,
        MovieAppendableResponses.videos,
      )
      Assert.assertNotNull("Images is null", response.images(ArtworkType.POSTER))
      response.images(ArtworkType.POSTER)?.forEach {
        println(it.toString())
      }
      Assert.assertNotNull("Videos is null", response.videos())
      response.videos()?.forEach {
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
