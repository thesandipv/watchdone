/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
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
) : WDEntity,
  TmdbIdEntity,
  Watchable {
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
) : WDEntity,
  TmdbIdEntity,
  Watchable
