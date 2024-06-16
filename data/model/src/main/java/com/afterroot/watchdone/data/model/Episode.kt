/*
 * Copyright (C) 2020-2023 Sandip Vaghela
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

data class EpisodeNew(
  val tmdb: TmdbEpisode,
  val isWatched: Boolean = false,
)
