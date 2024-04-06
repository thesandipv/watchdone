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

package com.afterroot.watchdone.media

import app.moviebase.tmdb.Tmdb3
import com.afterroot.watchdone.data.mapper.TmdbMovieDetailToMedia
import com.afterroot.watchdone.data.mapper.TmdbShowDetailToMedia
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.model.MediaType
import javax.inject.Inject

class TmdbMediaDataSource @Inject constructor(
  private val tmdb: Tmdb3,
  private val movieMapper: TmdbMovieDetailToMedia,
  private val showMapper: TmdbShowDetailToMedia,
) : MediaDataSource {
  override suspend fun getMedia(media: Media): Media {
    val tmdbId = media.tmdbId
      ?: throw IllegalArgumentException("TmdbId for movie/show does not exist [$media]")

    val result = when (media.mediaType) {
      MediaType.MOVIE -> movieMapper.map(tmdb.movies.getDetails(tmdbId))
      MediaType.SHOW -> showMapper.map(tmdb.show.getDetails(tmdbId))
      else -> throw IllegalArgumentException("MediaType ${media.mediaType} not supported")
    }

    return result
  }
}
