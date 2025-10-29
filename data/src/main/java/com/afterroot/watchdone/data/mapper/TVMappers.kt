/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.data.mapper

import app.moviebase.tmdb.model.TmdbShowDetail
import com.afterroot.watchdone.data.model.DBMedia
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.data.model.TV
import kotlinx.datetime.LocalDate

fun TmdbShowDetail.toTV(isWatched: Boolean = false): TV = TV(
  adult = false, // TODO Add to TmdbShowDetail
  backdropPath = backdropPath,
  createdBy = createdBy,
  episodeRuntime = episodeRuntime,
  firstAirDate = firstAirDate,
  genres = genres,
  homepage = homepage,
  inProduction = inProduction,
  languages = languages,
  lastAirDate = lastAirDate,
  lastEpisodeToAir = lastEpisodeToAir,
  name = name,
  networks = networks,
  nextEpisodeToAir = nextEpisodeToAir,
  numberOfEpisodes = numberOfEpisodes,
  numberOfSeasons = numberOfSeasons,
  originalLanguage = originalLanguage,
  originalName = originalName,
  originCountry = originCountry,
  overview = overview,
  popularity = popularity,
  posterPath = posterPath,
  productionCompanies = productionCompanies,
  seasons = seasons,
  status = status,
  tagline = tagline,
  tmdbId = id,
  type = type,
  voteAverage = voteAverage,
  voteCount = voteCount,

  // Appendable responses
  aggregateCredits = aggregateCredits,
  contentRatings = contentRatings,
  credits = credits,
  externalIds = externalIds,
  images = images,
  videos = videos,
  watchProviders = watchProviders,

  // Additional Data
  isWatched = isWatched,
)

fun TV.toDBMedia() = DBMedia(
  id = id.toInt(),
  releaseDate = releaseDate.toString(),
  title = name,
  isWatched = isWatched,
  posterPath = posterPath,
  mediaType = MediaType.SHOW,
  rating = voteAverage,
)

fun DBMedia.toTV(): TV = TV(
  id = id.toLong(),
  firstAirDate = releaseDate?.let { LocalDate.parse(it) },
  name = title,
  isWatched = isWatched,
  posterPath = posterPath,
  voteAverage = rating ?: 0f,
)

fun List<TmdbShowDetail>.toTV(): List<TV> = mapNotNull { it.toTV() }
