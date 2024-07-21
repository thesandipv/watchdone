/*
 * Copyright (C) 2020-2024 Sandip Vaghela
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

import app.moviebase.tmdb.model.TmdbCompany
import app.moviebase.tmdb.model.TmdbCountry
import app.moviebase.tmdb.model.TmdbCredits
import app.moviebase.tmdb.model.TmdbExternalIds
import app.moviebase.tmdb.model.TmdbGenre
import app.moviebase.tmdb.model.TmdbImages
import app.moviebase.tmdb.model.TmdbMovieDetail
import app.moviebase.tmdb.model.TmdbMovieStatus
import app.moviebase.tmdb.model.TmdbReleaseDates
import app.moviebase.tmdb.model.TmdbResult
import app.moviebase.tmdb.model.TmdbVideo
import app.moviebase.tmdb.model.TmdbWatchProviderResult
import java.util.Locale
import kotlinx.datetime.LocalDate

data class Movie(
  override val id: Long = 0,
  override val tmdbId: Int? = null,

  val adult: Boolean = false,
  val backdropPath: String? = null,
  val budget: Long = 0L,
  val genres: List<TmdbGenre> = emptyList(),
  val homepage: String? = null,
  val imdbId: String? = null,
  val originalLanguage: String? = null,
  val originalTitle: String? = null,
  val overview: String? = null,
  val popularity: Float = 0f,
  val posterPath: String? = null,
  val productionCompanies: List<TmdbCompany>? = null,
  val productionCountries: List<TmdbCountry>? = null,
  val releaseDate: LocalDate? = null,
  val revenue: Long = 0L,
  val runtime: Int? = null,
  val status: TmdbMovieStatus = TmdbMovieStatus.PLANNED,
  val tagline: String? = null,
  val title: String? = null,
  val video: Boolean = false,
  val voteAverage: Float = 0f,
  val voteCount: Int = 0,

  // Appendable responses
  val credits: TmdbCredits? = null,
  val externalIds: TmdbExternalIds? = null,
  val images: TmdbImages? = null,
  val releaseDates: TmdbResult<TmdbReleaseDates>? = null,
  val videos: TmdbResult<TmdbVideo>? = null,
  val watchProviders: TmdbWatchProviderResult? = null,

  // Additional Data
  override val isWatched: Boolean = false,
) : WDEntity, TmdbIdEntity, Watchable {
  val mediaType: MediaType
    get() = MediaType.MOVIE

  fun rating(): String = String.format(Locale.getDefault(), "%.1f", voteAverage)

  companion object {
    val Empty = Movie()
  }
}

data class MovieNew(
  val tmdb: TmdbMovieDetail,
  override val id: Long,
  override val tmdbId: Int?,
  override val isWatched: Boolean?,
) : WDEntity, TmdbIdEntity, Watchable
