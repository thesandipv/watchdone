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

import com.afterroot.watchdone.data.repositories.RecommendedShowsRepository
import com.afterroot.watchdone.domain.ResultInteractor
import com.afterroot.watchdone.utils.State
import info.movito.themoviedbapi.TvResultsPage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateRecommendedShows @Inject constructor(private val recommendedShowsRepository: RecommendedShowsRepository) :
    ResultInteractor<UpdateRecommendedShows.Params, Flow<State<TvResultsPage>>>() {
    data class Params(val id: Int, val page: Int = 1)

    override suspend fun doWork(params: Params): Flow<State<TvResultsPage>> {
        return recommendedShowsRepository.getRecommended(params.id, params.page)
    }
}
