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
import com.afterroot.watchdone.data.mapper.toMovies
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.domain.interactors.GetDiscoverMovies
import com.afterroot.watchdone.utils.State
import info.movito.themoviedbapi.model.Discover
import timber.log.Timber

@Deprecated("")
class DiscoverMoviePagingSource(
    private val discover: Discover,
    private val getDiscoverMovies: GetDiscoverMovies,
) :
    PagingSource<Int, Movie>() {
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        try {
            var nextPage = params.key ?: 1
            Timber.d("load: Page $nextPage")
            val response = getDiscoverMovies.executeSync(
                GetDiscoverMovies.Params(discover.page(nextPage)),
            )
            var loadResult: LoadResult<Int, Movie>? = null
            response.collect {
                when (it) {
                    is State.Success -> {
                        nextPage = it.data.page + 1

                        loadResult = LoadResult.Page(
                            data = it.data.toMovies(),
                            prevKey = null,
                            nextKey = if (nextPage <= it.data.totalPages) nextPage else null,
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
            return loadResult ?: LoadResult.Invalid()
        } catch (e: Exception) {
            Timber.e(e, "load: Load Error")
            return LoadResult.Error(e)
        }
    }
}
