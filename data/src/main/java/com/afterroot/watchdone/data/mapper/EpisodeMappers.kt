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
package com.afterroot.watchdone.data.mapper

import com.afterroot.watchdone.data.model.Episode
import com.afterroot.watchdone.data.model.Episodes
import com.afterroot.watchdone.data.model.TVEpisodes
import info.movito.themoviedbapi.model.tv.TvEpisode

fun TvEpisode.toEpisode(): Episode = Episode(
    id = id,
    name = name,
    airDate = airDate,
    episodeNumber = episodeNumber,
    overview = overview,
    seasonNumber = seasonNumber,
    seriesId = seriesId,
    stillPath = stillPath,
    userRating = userRating,
    voteAverage = voteAverage,
    voteCount = voteCount,
    credits = credits,
    externalIds = externalIds,
    images = images,
    videos = getVideos(),
    keywords = getKeywords(),
)

fun TVEpisodes.toEpisodes(): Episodes = this?.map {
    it.toEpisode()
}
