/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.media

import app.moviebase.tmdb.Tmdb3
import com.afterroot.watchdone.data.mapper.TmdbMovieDetailToMedia
import com.afterroot.watchdone.data.model.Media
import javax.inject.Inject

class TmdbMovieDataSource @Inject constructor(
  private val tmdb: Tmdb3,
  private val movieMapper: TmdbMovieDetailToMedia,
) : MovieDataSource {
  override suspend fun getMovie(media: Media): Media {
    val tmdbId = media.tmdbId
      ?: throw IllegalArgumentException("TmdbId for movie does not exist [$media]")

    return movieMapper.map(tmdb.movies.getDetails(tmdbId))
  }
}
