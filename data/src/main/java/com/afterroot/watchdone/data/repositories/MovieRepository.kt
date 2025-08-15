/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.repositories

import app.moviebase.tmdb.Tmdb3
import com.afterroot.watchdone.data.mapper.TmdbWatchProviderResultToWatchProviderResult
import com.afterroot.watchdone.data.mapper.toMovie
import com.afterroot.watchdone.utils.resultFlow
import javax.inject.Inject

class MovieRepository @Inject constructor(
  private val tmdb: Tmdb3,
  private val watchProviderMapper: TmdbWatchProviderResultToWatchProviderResult,
) {
  suspend fun credits(id: Int) = resultFlow(tmdb.movies.credits(id))
  suspend fun info(id: Int) = resultFlow(tmdb.movies.getDetails(id).toMovie())
  suspend fun recommended(id: Int, page: Int) = resultFlow(tmdb.movies.getRecommendations(id, page))
  suspend fun watchProviders(id: Int) = resultFlow(
    watchProviderMapper.map(tmdb.movies.getWatchProviders(id)),
  )
}
