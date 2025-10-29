/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.data.model

import app.moviebase.tmdb.model.TmdbEpisode
import app.moviebase.tmdb.model.TmdbExternalIds
import app.moviebase.tmdb.model.TmdbImages
import app.moviebase.tmdb.model.TmdbResult
import app.moviebase.tmdb.model.TmdbSeasonDetail
import app.moviebase.tmdb.model.TmdbVideo
import kotlinx.datetime.LocalDate

data class Season(
  val airDate: LocalDate? = null,
  val episodeCount: Int? = null,
  val episodes: List<TmdbEpisode>? = null,
  val id: Int,
  val name: String,
  val overview: String,
  val posterPath: String?,
  val seasonNumber: Int,
  val voteAverage: Float? = null,

  // Appendable responses
  val videos: TmdbResult<TmdbVideo>? = null,
  val images: TmdbImages? = null,
  val externalIds: TmdbExternalIds? = null,

  // Additional Data
  var isWatched: Boolean = false,
)

data class SeasonNew(val tmdb: TmdbSeasonDetail, val isWatched: Boolean = false)
