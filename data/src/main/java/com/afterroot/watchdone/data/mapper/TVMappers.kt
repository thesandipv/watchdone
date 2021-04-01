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

import com.afterroot.tmdbapi.TvResultsPage
import com.afterroot.tmdbapi.model.tv.TvSeries
import com.afterroot.watchdone.data.model.TV

fun TvSeries.toTV(isWatched: Boolean = false): TV = TV(
    id = id,
    createdBy = createdBy,
    episodeRuntime = episodeRuntime,
    firstAirDate = firstAirDate,
    lastAirDate = lastAirDate,
    genres = genres,
    homepage = homepage,
    originalName = originalName,
    originCountry = originCountry,
    networks = networks,
    overview = overview,
    popularity = popularity,
    backdropPath = backdropPath,
    posterPath = posterPath,
    numberOfEpisodes = numberOfEpisodes,
    numberOfSeasons = numberOfSeasons,
    seasons = seasons,
    recommendations = recommendations,
    userRating = userRating,
    voteAverage = voteAverage,
    voteCount = voteCount,
    status = status,
    // Appendable Responses
    contentRatings = getContentRatings(),
    credits = credits,
    externalIds = externalIds,
    images = images,
    videos = videos,
    keywords = keywords,
    // Additional
    isWatched = isWatched
)

fun TvResultsPage.toTV(): List<TV> = results.mapNotNull { it?.toTV() }
