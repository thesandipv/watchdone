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

import com.afterroot.watchdone.data.model.Season
import com.afterroot.watchdone.data.model.Seasons
import com.afterroot.watchdone.data.model.TVSeasons
import info.movito.themoviedbapi.model.tv.TvSeason

fun TvSeason.toSeason(): Season = Season(
    id = id,
    name = name,
    airDate = airDate,
    posterPath = posterPath,
    seasonNumber = seasonNumber,
    overview = overview,
    episodes = episodes.toEpisodes(),
    credits = credits,
    externalIds = externalIds,
    images = images,
    videos = getVideos(),
    keywords = getKeywords()
)

fun TVSeasons.toSeasons(): Seasons = this?.map {
    it.toSeason()
}
