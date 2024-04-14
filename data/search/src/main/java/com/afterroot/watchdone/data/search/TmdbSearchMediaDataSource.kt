/*
 * Copyright (C) 2020-2023 Sandip Vaghela
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

package com.afterroot.watchdone.data.search

import app.moviebase.tmdb.Tmdb3
import app.tivi.util.Logger
import com.afterroot.watchdone.data.mapper.TmdbMoviePageResultToMedias
import com.afterroot.watchdone.data.mapper.TmdbShowPageResultToMedias
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.model.MediaType
import javax.inject.Inject

class TmdbSearchMediaDataSource @Inject constructor(
  private val tmdb: Tmdb3,
  private val tmdbMoviePageResultToMedias: TmdbMoviePageResultToMedias,
  private val tmdbShowPageResultToMedias: TmdbShowPageResultToMedias,
  private val logger: Logger,
) : SearchDataSource {
  override suspend fun search(params: SearchDataSource.Params): List<Media> {
    return when (params.mediaType) {
      MediaType.MOVIE -> {
        logger.d { "Searching for: $params" }
        tmdbMoviePageResultToMedias.map(
          tmdb.search.findMovies(query = params.query, page = params.page),
        )
      }

      MediaType.SHOW -> {
        logger.d { "Searching for: $params" }
        tmdbShowPageResultToMedias.map(
          tmdb.search.findShows(query = params.query, page = params.page),
        )
      }

      else -> throw IllegalArgumentException("${params.mediaType} not supported")
    }
  }
}
