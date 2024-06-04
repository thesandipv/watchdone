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

import app.moviebase.tmdb.model.TmdbSeasonDetail
import com.afterroot.watchdone.data.model.Season
import com.afterroot.watchdone.data.model.Seasons
import com.afterroot.watchdone.data.model.TmdbSeasons

fun TmdbSeasonDetail.toSeason(): Season = Season(
  airDate = airDate,
  episodeCount = episodeCount,
  episodes = episodes,
  id = id,
  name = name,
  overview = overview,
  posterPath = posterPath,
  seasonNumber = seasonNumber,
  voteAverage = voteAverage,

  // Appendable responses
  videos = videos,
  images = images,
  externalIds = externalIds,

  // Additional Data
  isWatched = false,
)

fun TmdbSeasons.toSeasons(): Seasons = this?.map {
  it.toSeason()
}
