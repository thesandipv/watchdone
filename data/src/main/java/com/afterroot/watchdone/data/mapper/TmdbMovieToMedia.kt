/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.mapper

import app.moviebase.tmdb.model.TmdbMovie
import app.moviebase.tmdb.model.TmdbMovieDetail
import app.moviebase.tmdb.model.TmdbMoviePageResult
import app.tivi.data.mappers.Mapper
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.model.MediaType
import javax.inject.Inject

class TmdbMovieToMedia @Inject constructor() : Mapper<TmdbMovie, Media> {
  override fun map(from: TmdbMovie) = Media(
    tmdbId = from.id,
    releaseDate = from.releaseDate.toString(),
    title = from.title,
    isWatched = false,
    posterPath = from.posterPath,
    mediaType = MediaType.MOVIE,
    rating = from.voteAverage,
  )
}

class TmdbMovieDetailToMedia @Inject constructor() : Mapper<TmdbMovieDetail, Media> {
  override fun map(from: TmdbMovieDetail) = Media(
    tmdbId = from.id,
    releaseDate = from.releaseDate.toString(),
    title = from.title,
    isWatched = false,
    posterPath = from.posterPath,
    mediaType = MediaType.MOVIE,
    rating = from.voteAverage,
  )
}

class TmdbMoviePageResultToMedias @Inject constructor(
  private val tmdbMovieToMedia: TmdbMovieToMedia,
) : Mapper<TmdbMoviePageResult, List<Media>> {
  override fun map(from: TmdbMoviePageResult): List<Media> = from.results.map(tmdbMovieToMedia::map)
}
