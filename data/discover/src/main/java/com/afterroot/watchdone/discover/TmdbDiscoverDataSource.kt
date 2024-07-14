/*
 * Copyright (C) 2020-2024 Sandip Vaghela
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

package com.afterroot.watchdone.discover

import app.moviebase.tmdb.Tmdb3
import app.moviebase.tmdb.model.TmdbDiscover
import app.moviebase.tmdb.model.TmdbMoviePageResult
import app.moviebase.tmdb.model.TmdbShowPageResult
import com.afterroot.watchdone.data.mapper.TmdbMovieToMedia
import com.afterroot.watchdone.data.mapper.TmdbShowToMedia
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.settings.Settings
import javax.inject.Inject

class TmdbDiscoverDataSource @Inject constructor(
  private val tmdb: Tmdb3,
  private val settings: Settings,
  private val tmdbMovieToMedia: TmdbMovieToMedia,
  private val tmdbShowToMedia: TmdbShowToMedia,
) : DiscoverDataSource {
  override suspend fun invoke(
    page: Int,
    mediaType: MediaType,
    tmdbDiscover: TmdbDiscover,
  ): List<Media> {
    return when (mediaType) {
      MediaType.MOVIE -> {
        val discover = tmdb.discover.discover(
          page,
          region = settings.country,
          tmdbDiscover = tmdbDiscover,
        ) as TmdbMoviePageResult

        discover.results.map(tmdbMovieToMedia::map)
      }

      MediaType.SHOW -> {
        val discover = tmdb.discover.discover(
          page,
          region = settings.country,
          tmdbDiscover = tmdbDiscover,
        ) as TmdbShowPageResult

        discover.results.map(tmdbShowToMedia::map)
      }

      else -> {
        emptyList()
      }
    }
  }
}
