/*
 * Copyright (C) 2020-2026 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.search

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.afterroot.watchdone.core.testing.AppTest
import com.afterroot.watchdone.data.model.MediaType
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TmdbSearchMediaDataSourceTest : AppTest() {
  @Inject lateinit var tmdbSearchMediaDataSource: TmdbSearchMediaDataSource

  @Test
  fun test_Movie_Search_Response() = runBlocking {
    val results = tmdbSearchMediaDataSource.search(
      SearchDataSource.Params(
        mediaType = MediaType.MOVIE,
        query = "Fight Club",
      ),
    )
    assertTrue(results.isNotEmpty())
    assertEquals(
      expected = "Fight Club",
      actual = results.first().title,
    )
  }

  @Test
  fun test_Show_Search_Response() = runBlocking {
    val results = tmdbSearchMediaDataSource.search(
      SearchDataSource.Params(
        mediaType = MediaType.SHOW,
        query = "Game of Thrones",
      ),
    )
    assertTrue(results.isNotEmpty())
    assertEquals(
      expected = "Game of Thrones",
      actual = results.first().title,
    )
  }
}
