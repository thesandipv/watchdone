/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.data.model

import app.moviebase.tmdb.model.TmdbCast
import app.moviebase.tmdb.model.TmdbCrew
import app.moviebase.tmdb.model.TmdbEpisode
import kotlinx.datetime.LocalDate

data class Episode(
  val id: Int,
  val seasonNumber: Int,
  val episodeNumber: Int,
  val airDate: LocalDate? = null,
  val crew: List<TmdbCrew>? = null,
  val guestStars: List<TmdbCast>? = null,
  val name: String? = null,
  val overview: String? = null,
  val stillPath: String? = null,
  val voteAverage: Float? = null,
  val voteCount: Int? = null,
  // Additional Data
  val isWatched: Boolean = false,
)

data class EpisodeNew(val tmdb: TmdbEpisode, val isWatched: Boolean = false)
