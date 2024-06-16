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

data class SeasonNew(
  val tmdb: TmdbSeasonDetail,
  val isWatched: Boolean = false,
)
