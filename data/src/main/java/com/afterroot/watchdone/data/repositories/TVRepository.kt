/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
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
