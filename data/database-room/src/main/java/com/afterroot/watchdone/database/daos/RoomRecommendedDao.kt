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
import com.afterroot.watchdone.data.compoundmodel.RecommendedEntryWithMedia
import com.afterroot.watchdone.data.daos.RecommendedDao
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.data.model.RecommendedEntry
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RoomRecommendedDao : RecommendedDao, RoomPaginatedEntryDao<RecommendedEntry, RecommendedEntryWithMedia> {

    @Transaction
    @Query(
        """
        SELECT * FROM recommended_entries 
        WHERE page = :page AND rec_of = :recOf 
        ORDER BY id ASC
        """,
    )
    abstract override fun entriesForPage(page: Int, recOf: Int): Flow<List<RecommendedEntry>>

    @Transaction
    @Query(
        """
        SELECT * FROM recommended_entries 
        WHERE media_type = :mediaType AND rec_of = :recOf 
        ORDER BY page ASC, id ASC
        """,
    )
    abstract override fun entriesPagingSource(
        recOf: Int,
        mediaType: MediaType,
    ): PagingSource<Int, RecommendedEntryWithMedia>

    @Query(
        """
        DELETE FROM recommended_entries 
        WHERE page = :page AND media_type = :mediaType AND rec_of = :ofMedia
        """,
    )
    abstract override suspend fun deletePage(page: Int, mediaType: MediaType, ofMedia: Int)

    @Query("DELETE FROM recommended_entries")
    abstract override suspend fun deleteAll()

    @Query("DELETE FROM recommended_entries WHERE media_type = :mediaType")
    abstract override suspend fun deleteAll(mediaType: MediaType)

    @Query(
        """
        SELECT MAX(page) FROM recommended_entries 
        WHERE media_type = :mediaType AND rec_of = :ofMedia
        """,
    )
    abstract override suspend fun getLastPage(mediaType: MediaType, ofMedia: Int): Int?
    override suspend fun deletePage(page: Int) {}
    override suspend fun getLastPage(): Int? = null
}
