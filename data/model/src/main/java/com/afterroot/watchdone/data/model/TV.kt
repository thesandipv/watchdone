/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.data.model

import app.moviebase.tmdb.model.TmdbAggregateCredits
import app.moviebase.tmdb.model.TmdbCompany
import app.moviebase.tmdb.model.TmdbContentRating
import app.moviebase.tmdb.model.TmdbCredits
import app.moviebase.tmdb.model.TmdbEpisode
import app.moviebase.tmdb.model.TmdbExternalIds
import app.moviebase.tmdb.model.TmdbGenre
import app.moviebase.tmdb.model.TmdbImages
import app.moviebase.tmdb.model.TmdbNetwork
import app.moviebase.tmdb.model.TmdbResult
import app.moviebase.tmdb.model.TmdbSeason
import app.moviebase.tmdb.model.TmdbShowCreatedBy
import app.moviebase.tmdb.model.TmdbShowDetail
import app.moviebase.tmdb.model.TmdbShowStatus
import app.moviebase.tmdb.model.TmdbShowType
import app.moviebase.tmdb.model.TmdbVideo
import app.moviebase.tmdb.model.TmdbWatchProviderResult
import java.util.Locale
import kotlinx.datetime.LocalDate

data class TV(
  // Info
  override val id: Long = 0,
  override val tmdbId: Int? = null,

  val adult: Boolean = false, // TODO Add to TmdbShowDetail
  val backdropPath: String? = null,
  val createdBy: List<TmdbShowCreatedBy>? = null,
  val episodeRuntime: List<Int> = emptyList(),
  val firstAirDate: LocalDate? = null,
  val genres: List<TmdbGenre> = emptyList(),
  val homepage: String? = null,
  val inProduction: Boolean = false,
  val languages: List<String> = emptyList(),
  val lastAirDate: LocalDate? = null,
  val lastEpisodeToAir: TmdbEpisode? = null,
  val name: String? = null,
  val networks: List<TmdbNetwork> = emptyList(),
  val nextEpisodeToAir: TmdbEpisode? = null,
  val numberOfEpisodes: Int = 0,
  val numberOfSeasons: Int = 0,
  val originalLanguage: String? = null,
  val originalName: String? = null,
  val originCountry: List<String> = emptyList(),
  val overview: String? = null,
  val popularity: Float = 0f,
  val posterPath: String? = null,
  val productionCompanies: List<TmdbCompany>? = null,
  val seasons: List<TmdbSeason> = emptyList(),
  val status: TmdbShowStatus? = null,
  val tagline: String? = null,
  val type: TmdbShowType? = null,
  val voteAverage: Float = 0f,
  val voteCount: Int = 0,

  // Appendable responses
  val aggregateCredits: TmdbAggregateCredits? = null,
  val contentRatings: TmdbResult<TmdbContentRating>? = null,
  val credits: TmdbCredits? = null,
  val externalIds: TmdbExternalIds? = null,
  val images: TmdbImages? = null,
  val videos: TmdbResult<TmdbVideo>? = null,
  val watchProviders: TmdbWatchProviderResult? = null,

  // Additional Data
  override val isWatched: Boolean = false,
) : WDEntity,
  TmdbIdEntity,
  Watchable {

  val mediaType: MediaType
    get() = MediaType.SHOW

  // Just for Firestore
  var releaseDate = firstAirDate

  fun rating(): String = String.format(Locale.getDefault(), "%.1f", voteAverage)

  companion object {
    val Empty = TV()
  }
}

data class ShowNew(
  val tmdb: TmdbShowDetail,
  override val id: Long,
  override val tmdbId: Int?,
  override val isWatched: Boolean?,
) : WDEntity,
  TmdbIdEntity,
  Watchable
