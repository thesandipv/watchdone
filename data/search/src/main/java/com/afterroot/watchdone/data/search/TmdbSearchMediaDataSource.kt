/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
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
  override suspend fun search(params: SearchDataSource.Params): List<Media> =
    when (params.mediaType) {
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
