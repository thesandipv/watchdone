/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.domain.interactors

import app.moviebase.tmdb.model.TmdbCredits
import app.tivi.domain.ResultInteractor
import app.tivi.domain.SubjectInteractor
import com.afterroot.watchdone.data.model.Episode
import com.afterroot.watchdone.data.model.Season
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.data.model.WatchProviderResult
import com.afterroot.watchdone.data.repositories.TVRepository
import com.afterroot.watchdone.utils.State
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class TVEpisodeInteractor @Inject constructor(private val tvRepository: TVRepository) :
  ResultInteractor<TVEpisodeInteractor.Params, Flow<State<Episode>>>() {

  data class Params(val tvId: Int, val season: Int, val episode: Int)

  override suspend fun doWork(params: Params): Flow<State<Episode>> =
    tvRepository.episode(params.tvId, params.season, params.episode)
}

class ObserveTVInfo @Inject constructor(private val tvRepository: TVRepository) :
  SubjectInteractor<ObserveTVInfo.Params, State<TV>>() {
  data class Params(val tvId: Int)

  override suspend fun createObservable(params: Params): Flow<State<TV>> =
    tvRepository.info(params.tvId)
}

class ObserveTVCredits @Inject constructor(private val tvRepository: TVRepository) :
  SubjectInteractor<ObserveTVCredits.Params, State<TmdbCredits>>() {
  data class Params(val tvId: Int)

  override suspend fun createObservable(params: Params): Flow<State<TmdbCredits>> =
    tvRepository.credits(params.tvId)
}

class ObserveTVSeason @Inject constructor(private val tvRepository: TVRepository) :
  SubjectInteractor<ObserveTVSeason.Params, State<Season>>() {

  data class Params(val tvId: Int, val season: Int)

  override suspend fun createObservable(params: Params): Flow<State<Season>> =
    tvRepository.season(params.tvId, params.season)
}

class ObserveTVWatchProviders @Inject constructor(private val tvRepository: TVRepository) :
  SubjectInteractor<ObserveTVWatchProviders.Params, State<WatchProviderResult>>() {
  data class Params(val tvId: Int)

  override suspend fun createObservable(params: Params): Flow<State<WatchProviderResult>> =
    tvRepository.watchProviders(params.tvId)
}
