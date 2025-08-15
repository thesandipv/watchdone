/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.mapper

import app.moviebase.tmdb.model.TmdbShow
import app.moviebase.tmdb.model.TmdbShowDetail
import app.moviebase.tmdb.model.TmdbShowPageResult
import app.tivi.data.mappers.Mapper
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.model.MediaType
import javax.inject.Inject

class TmdbShowToMedia @Inject constructor() : Mapper<TmdbShow, Media> {
  override fun map(from: TmdbShow) = Media(
    tmdbId = from.id,
    releaseDate = from.firstAirDate.toString(),
    title = from.name,
    isWatched = false,
    posterPath = from.posterPath,
    mediaType = MediaType.SHOW,
    rating = from.voteAverage,
  )
}

class TmdbShowDetailToMedia @Inject constructor() : Mapper<TmdbShowDetail, Media> {
  override fun map(from: TmdbShowDetail) = Media(
    tmdbId = from.id,
    releaseDate = from.firstAirDate.toString(),
    title = from.name,
    isWatched = false,
    posterPath = from.posterPath,
    mediaType = MediaType.SHOW,
    rating = from.voteAverage,
  )
}

class TmdbShowPageResultToMedias @Inject constructor(
  private val tmdbShowToMedia: TmdbShowToMedia,
) : Mapper<TmdbShowPageResult, List<Media>> {
  override fun map(from: TmdbShowPageResult): List<Media> = from.results.map(tmdbShowToMedia::map)
}
