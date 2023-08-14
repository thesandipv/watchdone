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

import com.afterroot.tmdbapi.api.DiscoverApi
import com.afterroot.watchdone.utils.State
import info.movito.themoviedbapi.model.Discover
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DiscoverRepository @Inject constructor(private val discoverApi: DiscoverApi) {
    fun getMoviesDiscover(discover: Discover) = flow {
        emit(State.loading())
        emit(State.success(discoverApi.getMoviesDiscover(discover.params)))
    }.catch {
        emit(State.failed("TODO")) // TODO
    }.flowOn(Dispatchers.IO)

    fun getTVDiscover(discover: Discover) = flow {
        emit(State.loading())
        emit(State.success(discoverApi.getTVDiscover(discover.params)))
    }.catch {
        emit(State.failed("TODO")) // TODO
    }.flowOn(Dispatchers.IO)
}
