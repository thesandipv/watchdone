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

import app.moviebase.tmdb.model.TmdbEpisode
import app.moviebase.tmdb.model.TmdbEpisodeDetail
import com.afterroot.watchdone.data.model.Episode
import com.afterroot.watchdone.data.model.Episodes
import com.afterroot.watchdone.data.model.TmdbEpisodeDetails
import com.afterroot.watchdone.data.model.TmdbEpisodes

fun TmdbEpisode.toEpisode(): Episode = Episode(
  id = id,
  seasonNumber = seasonNumber,
  episodeNumber = episodeNumber,
  airDate = airDate,
  crew = crew,
  guestStars = guestStars,
  name = name,
  overview = overview,
  stillPath = stillPath,
  voteAverage = voteAverage,
  voteCount = voteCount,
)

fun TmdbEpisodeDetail.toEpisode(): Episode = Episode(
  id = id,
  seasonNumber = seasonNumber,
  episodeNumber = episodeNumber,
  airDate = airDate,
  crew = crew,
  guestStars = guestStars,
  name = name,
  overview = overview,
  stillPath = stillPath,
  voteAverage = voteAverage,
  voteCount = voteCount,
)

fun TmdbEpisodes.toEpisodes(): Episodes = this?.map {
  it.toEpisode()
}

fun TmdbEpisodeDetails.asEpisodes(): Episodes = this?.map {
  it.toEpisode()
}
