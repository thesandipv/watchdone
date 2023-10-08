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

package com.afterroot.watchdone.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.afterroot.watchdone.database.model.MediaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreMedia(entities: List<MediaEntity>)

    @Upsert
    suspend fun upsertMedia(entities: List<MediaEntity>)

    @Query(
        value = """
        SELECT * FROM media
        WHERE id IS :id
    """,
    )
    fun getMedia(id: Int): MediaEntity

    @Query(
        value = """
        SELECT * FROM media
        WHERE
            CASE WHEN :filterIds 
                THEN id IN (:ids) 
                ELSE 1 
            END
    """,
    )
    fun getMedia(ids: Set<Int>, filterIds: Boolean = false): Flow<List<MediaEntity>>

    @Query(
        value = """
        DELETE FROM media
        WHERE id IS :id
    """,
    )
    suspend fun deleteMedia(id: Int)

    @Query(
        value = """
        DELETE FROM media
        WHERE id IN (:ids)
    """,
    )
    suspend fun deleteMedia(ids: List<Int>)
}
