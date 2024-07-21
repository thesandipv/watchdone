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

import app.moviebase.tmdb.model.TmdbMovieDetail
import com.afterroot.watchdone.data.model.DBMedia
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.data.model.Movie
import kotlinx.datetime.LocalDate

/**
 * Maps [TmdbMovieDetail] to [Movie]
 */
fun TmdbMovieDetail.toMovie(isWatched: Boolean = false): Movie = Movie(
  adult = adult,
  backdropPath = backdropPath,
  budget = budget,
  genres = genres,
  homepage = homepage,
  imdbId = imdbId,
  originalLanguage = originalLanguage,
  originalTitle = originalTitle,
  overview = overview,
  popularity = popularity,
  posterPath = posterPath,
  productionCompanies = productionCompanies,
  productionCountries = productionCountries,
  releaseDate = releaseDate,
  revenue = revenue,
  runtime = runtime,
  status = status,
  tagline = tagline,
  title = title,
  tmdbId = id,
  video = video,
  voteAverage = voteAverage,
  voteCount = voteCount,

  // Appendable responses
  credits = credits,
  externalIds = externalIds,
  images = images,
  releaseDates = releaseDates,
  videos = videos,
  watchProviders = watchProviders,

  // Additional Data
  isWatched = isWatched,
)

fun DBMedia.toMovie(): Movie = Movie(
  tmdbId = id,
  releaseDate = releaseDate?.let { LocalDate.parse(it) },
  title = title,
  isWatched = isWatched,
  posterPath = posterPath,
  voteAverage = rating ?: 0f,
)

fun DBMedia.toMedia(): Media = Media(
  tmdbId = id,
  releaseDate = releaseDate,
  title = title,
  isWatched = isWatched,
  posterPath = posterPath,
  rating = rating,
  mediaType = mediaType,
)

fun Movie.toDBMedia() = DBMedia(
  id = tmdbId ?: throw NullPointerException("tmdbId is null"),
  releaseDate = releaseDate.toString(),
  title = title,
  isWatched = isWatched,
  posterPath = posterPath,
  mediaType = MediaType.MOVIE,
  rating = voteAverage,
)

fun List<TmdbMovieDetail>.toMovies(): List<Movie> = mapNotNull { it.toMovie() }
