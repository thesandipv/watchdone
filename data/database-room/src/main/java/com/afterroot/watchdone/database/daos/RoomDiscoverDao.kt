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

package com.afterroot.watchdone.database.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import app.tivi.data.daos.RoomPaginatedEntryDao
import com.afterroot.watchdone.data.compoundmodel.DiscoverEntryWithMedia
import com.afterroot.watchdone.data.daos.DiscoverDao
import com.afterroot.watchdone.data.model.DiscoverEntry
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RoomDiscoverDao : DiscoverDao, RoomPaginatedEntryDao<DiscoverEntry, DiscoverEntryWithMedia> {
    @Transaction
    @Query("SELECT * FROM discover_entries WHERE page = :page ORDER BY id ASC")
    abstract override fun entriesForPage(page: Int): Flow<List<DiscoverEntry>>

    @Transaction
    @Query("SELECT * FROM discover_entries ORDER BY page ASC, id ASC LIMIT :count OFFSET :offset")
    abstract override fun entriesObservable(
        count: Int,
        offset: Int,
    ): Flow<List<DiscoverEntryWithMedia>>

    @Transaction
    @Query("SELECT * FROM discover_entries ORDER BY page ASC, id ASC")
    abstract override fun entriesPagingSource(): PagingSource<Int, DiscoverEntryWithMedia>

    @Query("DELETE FROM discover_entries WHERE page = :page")
    abstract override suspend fun deletePage(page: Int)

    @Query("DELETE FROM discover_entries")
    abstract override suspend fun deleteAll()

    @Query("SELECT MAX(page) FROM discover_entries")
    abstract override suspend fun getLastPage(): Int?
}
