/*
 * Copyright (C) 2020-2021 Sandip Vaghela
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

import com.afterroot.tmdbapi.model.NetworkMovie
import com.afterroot.tmdbapi.model.core.MovieResultsPage
import com.afterroot.watchdone.data.model.DbMovie
import com.afterroot.watchdone.data.model.Movie

/**
 * Maps [NetworkMovie] to [Movie]
 */
fun NetworkMovie.toMovie(isWatched: Boolean = false): Movie = Movie(
    id = id,
    adult = adult,
    backdropPath = backdropPath,
    belongsToCollection = belongsToCollection,
    budget = budget,
    genreIds = genreIds,
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
    spokenLanguages = spokenLanguages,
    status = status,
    tagline = tagline,
    title = title,
    video = video,
    voteAverage = voteAverage,
    voteCount = voteCount,
    userRating = userRating,
    recommendedMovies = recommendedMovies,
    alternativeTitles = alternativeTitles(),
    images = images,
    keywords = keywords,
    lists = lists,
    releases = releases,
    reviews = reviews,
    similarMovies = similarMovies,
    translations = translations,
    videos = videos,
    credits = credits,
    isWatched = isWatched
)

fun DbMovie.toMovie(): Movie = Movie(
    id = id,
    releaseDate = releaseDate,
    title = title,
    isWatched = isWatched ?: false,
    posterPath = posterPath,
)

fun Movie.toDbMovie() = DbMovie(
    id = id, releaseDate = releaseDate, title = title, isWatched = isWatched, posterPath = posterPath
)

fun MovieResultsPage.toMovies(): List<Movie> = results.mapNotNull { it?.toMovie() }
