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
import com.afterroot.tmdbapi.model.Query
import com.afterroot.watchdone.data.repositories.SearchRepository
import com.afterroot.watchdone.utils.State
import info.movito.themoviedbapi.TvResultsPage
import info.movito.themoviedbapi.model.core.MovieResultsPage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchMovieInteractor @Inject constructor(private val searchRepository: SearchRepository) :
    ResultInteractor<SearchMovieInteractor.Params, Flow<State<MovieResultsPage>>>() {

    data class Params(val query: Query)

    override suspend fun doWork(params: Params): Flow<State<MovieResultsPage>> {
        return searchRepository.searchMovie(params.query)
    }
}

class SearchTVInteractor @Inject constructor(private val searchRepository: SearchRepository) :
    ResultInteractor<SearchTVInteractor.Params, Flow<State<TvResultsPage>>>() {

    data class Params(val query: Query)

    override suspend fun doWork(params: Params): Flow<State<TvResultsPage>> {
        return searchRepository.searchTV(params.query)
    }
}
