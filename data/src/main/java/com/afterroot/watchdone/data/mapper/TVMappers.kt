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
