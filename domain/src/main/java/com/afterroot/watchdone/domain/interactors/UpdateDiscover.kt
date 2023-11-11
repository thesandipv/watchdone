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

import app.tivi.data.util.fetch
import app.tivi.domain.Interactor
import app.tivi.util.Logger
import app.tivi.util.parallelForEach
import com.afterroot.watchdone.base.CoroutineDispatchers
import com.afterroot.watchdone.data.daos.DiscoverDao
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.discover.DiscoverMovieStore
import com.afterroot.watchdone.discover.DiscoverShowsStore
import com.afterroot.watchdone.media.MovieStore
import javax.inject.Inject
import kotlinx.coroutines.withContext

class UpdateDiscover @Inject constructor(
    private val discoverMovieStore: DiscoverMovieStore,
    private val discoverShowStore: DiscoverShowsStore,
    private val discoverDao: DiscoverDao,
    private val mediaStore: MovieStore,
    private val dispatchers: CoroutineDispatchers,
    private val logger: Logger,
) : Interactor<UpdateDiscover.Params, Unit>() {
    data class Params(val mediaType: MediaType, val page: Int, val forceRefresh: Boolean = false)

    object Page {
        const val NEXT_PAGE = -1
        const val REFRESH = -2
    }

    override suspend fun doWork(params: Params) {
        withContext(dispatchers.io) {
            val page = when {
                params.page >= 0 -> params.page
                params.page == Page.NEXT_PAGE -> {
                    val lastPage = discoverDao.getLastPage()
                    if (lastPage != null) lastPage + 1 else 0
                }

                else -> 1
            }
            logger.d { "APPEND: Fetching page $page" }
            when (params.mediaType) {
                MediaType.MOVIE -> {
                    discoverMovieStore.fetch(page, params.forceRefresh).parallelForEach {
                        mediaStore.fetch(it.mediaId)
                    }
                }

                MediaType.SHOW -> {
                    discoverShowStore.fetch(page, params.forceRefresh).parallelForEach {
                        mediaStore.fetch(it.mediaId)
                    }
                }

                else -> {}
            }
        }
    }
}
