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
package com.afterroot.watchdone.domain.observers

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.afterroot.watchdone.data.mapper.toTV
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.domain.interactors.ObserveRecommendedShows
import com.afterroot.watchdone.utils.State
import timber.log.Timber

class RecommendedShowPagingSource(
    private val showId: Int,
    private val observeRecommendedShows: ObserveRecommendedShows
) : PagingSource<Int, TV>() {

    override fun getRefreshKey(state: PagingState<Int, TV>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TV> {
        try {
            var nextPage = params.key ?: 1
            val response = observeRecommendedShows.executeSync(
                ObserveRecommendedShows.Params(showId, nextPage)
            )
            var loadResult: LoadResult<Int, TV>? = null
            response.collect {
                when (it) {
                    is State.Success -> {
                        nextPage = it.data.page + 1

                        loadResult = LoadResult.Page(
                            data = if (it.data.page == 1) it.data.toTV().dropLast(1) else it.data.toTV(),
                            prevKey = null,
                            nextKey = if (nextPage <= it.data.totalPages) nextPage else null
                        )
                    }

                    is State.Loading -> {
                        Timber.d("load: Loading Page $nextPage")
                    }

                    is State.Failed -> {
                        Timber.d("load: Failed loading page")
                    }
                }
            }
            return loadResult!!
        } catch (e: Exception) {
            Timber.e(e, "load: Load Error")
            return LoadResult.Error(e)
        }
    }
}
