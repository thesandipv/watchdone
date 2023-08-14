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

package com.afterroot.watchdone.data.repositories

import com.afterroot.tmdbapi.api.MoviesApi
import com.afterroot.watchdone.data.mapper.toMovie
import com.afterroot.watchdone.utils.State
import com.afterroot.watchdone.utils.resultFlow
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MovieRepository @Inject constructor(private val moviesApi: MoviesApi) {

    // TODO Migrate to resultFlow()
    fun credits(id: Int) = flow {
        emit(State.loading())
        emit(State.success(moviesApi.getCredits(id)))
    }.catch {
        emit(State.failed("TODO")) // TODO
    }.flowOn(Dispatchers.IO)

    suspend fun info(id: Int) = resultFlow(moviesApi.getMovieInfo(id).toMovie())

    suspend fun recommended(id: Int, page: Int) = resultFlow(moviesApi.getRecommended(id, page))

    suspend fun watchProviders(id: Int) = resultFlow(moviesApi.getWatchProviders(id))
}
