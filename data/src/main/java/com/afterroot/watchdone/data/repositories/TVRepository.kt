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

package com.afterroot.watchdone.data.repositories

import app.moviebase.tmdb.Tmdb3
import com.afterroot.watchdone.data.mapper.TmdbWatchProviderResultToWatchProviderResult
import com.afterroot.watchdone.data.mapper.toEpisode
import com.afterroot.watchdone.data.mapper.toSeason
import com.afterroot.watchdone.data.mapper.toTV
import com.afterroot.watchdone.utils.resultFlow
import javax.inject.Inject

class TVRepository @Inject constructor(
  private val tmdb: Tmdb3,
  private val watchProviderMapper: TmdbWatchProviderResultToWatchProviderResult,
) {

  suspend fun season(id: Int, season: Int) = resultFlow(
    tmdb.showSeasons.getDetails(id, season).toSeason(),
  )

  suspend fun episode(id: Int, season: Int, episode: Int) = resultFlow(
    tmdb.showEpisodes.getDetails(id, season, episode).toEpisode(),
  )

  suspend fun credits(id: Int) = resultFlow(tmdb.show.credits(id))

  suspend fun info(id: Int) = resultFlow(tmdb.show.getDetails(id).toTV())

  suspend fun recommended(id: Int, page: Int) = resultFlow(tmdb.show.getRecommendations(id, page))

  suspend fun watchProviders(id: Int) = resultFlow(
    watchProviderMapper.map(tmdb.show.getWatchProviders(id)),
  )
}
