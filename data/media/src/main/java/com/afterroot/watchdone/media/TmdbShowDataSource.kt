/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.media

import app.moviebase.tmdb.Tmdb3
import com.afterroot.watchdone.data.mapper.TmdbShowDetailToMedia
import com.afterroot.watchdone.data.model.Media
import javax.inject.Inject

class TmdbShowDataSource @Inject constructor(
  private val tmdb: Tmdb3,
  private val showMapper: TmdbShowDetailToMedia,
) : ShowDataSource {
  override suspend fun getShow(media: Media): Media {
    val tmdbId = media.tmdbId
      ?: throw IllegalArgumentException("TmdbId for show does not exist [$media]")

    return showMapper.map(tmdb.show.getDetails(tmdbId))
  }
}
