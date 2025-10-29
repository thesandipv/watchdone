/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.mapper

import app.moviebase.tmdb.model.TmdbMediaType
import app.tivi.data.mappers.Mapper
import com.afterroot.watchdone.data.model.MediaType
import javax.inject.Inject

class TmdbMediaTypeToMediaType @Inject constructor() : Mapper<TmdbMediaType, MediaType> {
  override fun map(from: TmdbMediaType): MediaType = when (from) {
    TmdbMediaType.MOVIE -> MediaType.MOVIE
    TmdbMediaType.SHOW -> MediaType.SHOW
    TmdbMediaType.SEASON -> MediaType.SEASON
    TmdbMediaType.EPISODE -> MediaType.EPISODE
  }
}

class MediaTypeToTmdbMediaType @Inject constructor() : Mapper<MediaType, TmdbMediaType> {
  override fun map(from: MediaType): TmdbMediaType = when (from) {
    MediaType.MOVIE -> TmdbMediaType.MOVIE
    MediaType.SHOW -> TmdbMediaType.SHOW
    MediaType.SEASON -> TmdbMediaType.SEASON
    MediaType.EPISODE -> TmdbMediaType.EPISODE
  }
}
