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
package com.afterroot.watchdone.domain.observers

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.afterroot.watchdone.data.mapper.toTV
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.domain.interactors.UpdateRecommendedShows
import com.afterroot.watchdone.utils.State
import timber.log.Timber
import javax.inject.Inject

/*
class ObserveRecommnedShows @Inject constructor(private val updateRecommendedShows: UpdateRecommendedShows) :
    PagingInteractor<ObserveRecommnedShows.Params, TvResultsPage>() {
    data class Params(
        override val pagingConfig: PagingConfig,
        val id: Int
    ) : PagingInteractor.Parameters<TvResultsPage>

    override fun createObservable(params: Params): Flow<PagingData<TvResultsPage>> {
        return Pager(config = params.pagingConfig, remoteMediator = ResultsPageRemoteMediator { page ->
            updateRecommendedShows.executeSync(UpdateRecommendedShows.Params(params.id, page))
        }).flow
    }
}*/

class RecommendedShowPagingSource @Inject constructor(val updateRecommendedShows: UpdateRecommendedShows) :
    PagingSource<RecommendedShowPagingSource.Params, TV>() {
    override fun getRefreshKey(state: PagingState<Params, TV>): Params? {
        return null
        /*return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            Params(anchorPage?.prevKey?.id, anchorPage?.prevKey?.page?.plus(1) ?: anchorPage?.nextKey?.page?.minus(1) ?: 1)
        }*/
    }

    data class Params(val id: Int?, val page: Int)

    override suspend fun load(params: LoadParams<Params>): LoadResult<Params, TV> {
        try {
            val nextPage = params.key?.page ?: 1
            val response = updateRecommendedShows.executeSync(UpdateRecommendedShows.Params(params.key?.id ?: 1, nextPage))
            var loadResult: LoadResult<Params, TV>? = null
            response.collect {
                when (it) {
                    is State.Success -> {
                        loadResult = LoadResult.Page(
                            data = it.data.toTV(),
                            prevKey = null,
                            nextKey = params.key?.copy(page = it.data.page + 1)
                        )
                    }
                    else -> {
                    }
                }
            }
            return loadResult!!
        } catch (e: Exception) {
            Timber.e("Paging", "load: Load Error")
            return LoadResult.Error(e)
        }
    }
}
