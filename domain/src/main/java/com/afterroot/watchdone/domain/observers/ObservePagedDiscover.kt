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

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import app.tivi.domain.PaginatedEntryRemoteMediator
import app.tivi.domain.PagingInteractor
import app.tivi.util.Logger
import com.afterroot.watchdone.data.compoundmodel.DiscoverEntryWithMedia
import com.afterroot.watchdone.data.daos.DiscoverDao
import com.afterroot.watchdone.domain.interactors.UpdateDiscover
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObservePagedDiscover @Inject constructor(
    private val discoverDao: DiscoverDao,
    private val updateDiscover: UpdateDiscover,
    private val logger: Logger,
) : PagingInteractor<ObservePagedDiscover.Params, DiscoverEntryWithMedia>() {

    data class Params(override val pagingConfig: PagingConfig) : Parameters<DiscoverEntryWithMedia>

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun createObservable(
        params: Params,
    ): Flow<PagingData<DiscoverEntryWithMedia>> {
        return Pager(
            config = params.pagingConfig,
            remoteMediator = PaginatedEntryRemoteMediator { page ->
                logger.d { "APPEND: Requesting Page: $page" }
                updateDiscover(UpdateDiscover.Params(page, true))
            },
            pagingSourceFactory = discoverDao::entriesPagingSource,
        ).flow
    }
}
