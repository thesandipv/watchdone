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

package com.afterroot.watchdone.domain.interactors

import app.tivi.domain.ResultInteractor
import app.tivi.domain.SubjectInteractor
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.repositories.MovieRepository
import com.afterroot.watchdone.utils.State
import info.movito.themoviedbapi.model.Credits
import info.movito.themoviedbapi.model.core.MovieResultsPage
import info.movito.themoviedbapi.model.providers.ProviderResults
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

@Deprecated("Use ObserveMovieCredits")
class MovieCreditsInteractor @Inject constructor(private val movieRepository: MovieRepository) :
    ResultInteractor<MovieCreditsInteractor.Params, Flow<State<Credits>>>() {

    data class Params(val movieId: Int)

    override suspend fun doWork(params: Params): Flow<State<Credits>> {
        return movieRepository.credits(params.movieId)
    }
}

class ObserveMovieCredits @Inject constructor(private val movieRepository: MovieRepository) :
    SubjectInteractor<ObserveMovieCredits.Params, State<Credits>>() {
    data class Params(val movieId: Int)

    override suspend fun createObservable(params: Params): Flow<State<Credits>> {
        return movieRepository.credits(params.movieId)
    }
}

class ObserveMovieInfo @Inject constructor(private val movieRepository: MovieRepository) :
    SubjectInteractor<ObserveMovieInfo.Params, State<Movie>>() {
    data class Params(val movieId: Int)

    override suspend fun createObservable(params: Params): Flow<State<Movie>> {
        return movieRepository.info(params.movieId)
    }
}

class ObserveRecommendedMovies @Inject constructor(private val movieRepository: MovieRepository) :
    ResultInteractor<ObserveRecommendedMovies.Params, Flow<State<MovieResultsPage>>>() {
    data class Params(val id: Int, val page: Int = 1)

    override suspend fun doWork(params: Params): Flow<State<MovieResultsPage>> {
        return movieRepository.recommended(params.id, params.page)
    }
}

class ObserveMovieWatchProviders @Inject constructor(private val movieRepository: MovieRepository) :
    SubjectInteractor<ObserveMovieWatchProviders.Params, State<ProviderResults>>() {
    data class Params(val id: Int)

    override suspend fun createObservable(params: Params): Flow<State<ProviderResults>> {
        return movieRepository.watchProviders(params.id)
    }
}
