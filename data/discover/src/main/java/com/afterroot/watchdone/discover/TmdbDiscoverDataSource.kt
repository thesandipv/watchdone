/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
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
  ): List<Media> = when (mediaType) {
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
