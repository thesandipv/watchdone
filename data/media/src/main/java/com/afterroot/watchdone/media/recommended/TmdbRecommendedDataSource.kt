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

package com.afterroot.watchdone.media.recommended

import app.moviebase.tmdb.Tmdb3
import app.tivi.data.mappers.map
import com.afterroot.watchdone.data.mapper.TmdbMovieToMedia
import com.afterroot.watchdone.data.mapper.TmdbShowToMedia
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.model.MediaType
import javax.inject.Inject

class TmdbRecommendedDataSource @Inject constructor(
  private val tmdb: Tmdb3,
  private val movieMapper: TmdbMovieToMedia,
  private val showMapper: TmdbShowToMedia,
) : RecommendedDataSource {
  override suspend fun invoke(mediaId: Int, mediaType: MediaType, page: Int): List<Media> =
    when (mediaType) {
      MediaType.MOVIE -> {
        tmdb.movies.getRecommendations(mediaId, page).results.let { movieMapper.map(it) }
      }

      MediaType.SHOW -> {
        tmdb.show.getRecommendations(mediaId, page).results.let { showMapper.map(it) }
      }

      else -> emptyList()
    }
}
