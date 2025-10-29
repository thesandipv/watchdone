/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
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
