/*
 * Copyright (C) 2020-2022 Sandip Vaghela
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

package com.afterroot.watchdone.domain.interactors

import app.tivi.domain.ResultInteractor
import app.tivi.domain.SubjectInteractor
import com.afterroot.watchdone.data.model.Episode
import com.afterroot.watchdone.data.model.Season
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.data.repositories.TVRepository
import com.afterroot.watchdone.utils.State
import info.movito.themoviedbapi.TvResultsPage
import info.movito.themoviedbapi.model.Credits
import info.movito.themoviedbapi.model.providers.ProviderResults
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@Deprecated("Use ObserveTVSeason")
class TVSeasonInteractor @Inject constructor(private val tvRepository: TVRepository) :
    ResultInteractor<TVSeasonInteractor.Params, Flow<State<Season>>>() {

    data class Params(val tvId: Int, val season: Int)

    override suspend fun doWork(params: Params): Flow<State<Season>> {
        return tvRepository.season(params.tvId, params.season)
    }
}

class TVEpisodeInteractor @Inject constructor(private val tvRepository: TVRepository) :
    ResultInteractor<TVEpisodeInteractor.Params, Flow<State<Episode>>>() {

    data class Params(val tvId: Int, val season: Int, val episode: Int)

    override suspend fun doWork(params: Params): Flow<State<Episode>> {
        return tvRepository.episode(params.tvId, params.season, params.episode)
    }
}

@Deprecated("Use ObserveTVCredits")
class TVCreditsInteractor @Inject constructor(private val tvRepository: TVRepository) :
    ResultInteractor<TVCreditsInteractor.Params, Flow<State<Credits>>>() {

    data class Params(val tvId: Int)

    override suspend fun doWork(params: Params): Flow<State<Credits>> {
        return tvRepository.credits(params.tvId)
    }
}

class ObserveTVInfo @Inject constructor(private val tvRepository: TVRepository) :
    SubjectInteractor<ObserveTVInfo.Params, State<TV>>() {
    data class Params(val tvId: Int)

    override suspend fun createObservable(params: Params): Flow<State<TV>> {
        return tvRepository.info(params.tvId)
    }
}

class ObserveRecommendedShows @Inject constructor(private val tvRepository: TVRepository) :
    ResultInteractor<ObserveRecommendedShows.Params, Flow<State<TvResultsPage>>>() {
    data class Params(val id: Int, val page: Int = 1)

    override suspend fun doWork(params: Params): Flow<State<TvResultsPage>> {
        return tvRepository.recommended(params.id, params.page)
    }
}

class ObserveTVCredits @Inject constructor(private val tvRepository: TVRepository) :
    SubjectInteractor<ObserveTVCredits.Params, State<Credits>>() {
    data class Params(val tvId: Int)

    override suspend fun createObservable(params: Params): Flow<State<Credits>> {
        return tvRepository.credits(params.tvId)
    }
}

class ObserveTVSeason @Inject constructor(private val tvRepository: TVRepository) :
    SubjectInteractor<ObserveTVSeason.Params, State<Season>>() {

    data class Params(val tvId: Int, val season: Int)

    override suspend fun createObservable(params: Params): Flow<State<Season>> {
        return tvRepository.season(params.tvId, params.season)
    }
}

class ObserveTVWatchProviders @Inject constructor(private val tvRepository: TVRepository) :
    SubjectInteractor<ObserveTVWatchProviders.Params, State<ProviderResults>>() {
    data class Params(val tvId: Int)

    override suspend fun createObservable(params: Params): Flow<State<ProviderResults>> {
        return tvRepository.watchProviders(params.tvId)
    }
}
