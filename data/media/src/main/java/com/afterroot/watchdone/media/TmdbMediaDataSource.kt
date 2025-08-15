/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
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
