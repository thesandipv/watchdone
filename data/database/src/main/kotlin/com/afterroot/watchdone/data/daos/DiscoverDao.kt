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

package com.afterroot.watchdone.data.daos

import androidx.paging.PagingSource
import app.tivi.data.daos.PaginatedEntryDao
import com.afterroot.watchdone.data.compoundmodel.DiscoverEntryWithMedia
import com.afterroot.watchdone.data.model.DiscoverEntry
import kotlinx.coroutines.flow.Flow

interface DiscoverDao : PaginatedEntryDao<DiscoverEntry, DiscoverEntryWithMedia> {
    override suspend fun deletePage(page: Int)
    override suspend fun deleteAll()
    override suspend fun getLastPage(): Int?
    fun entriesForPage(page: Int): Flow<List<DiscoverEntry>>
    fun entriesObservable(count: Int, offset: Int): Flow<List<DiscoverEntryWithMedia>>
    fun entriesPagingSource(): PagingSource<Int, DiscoverEntryWithMedia>
}
