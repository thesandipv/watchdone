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

import androidx.room.Dao
import androidx.room.Query
import app.tivi.data.daos.RoomEntityDao
import com.afterroot.watchdone.data.daos.MediaDao
import com.afterroot.watchdone.data.model.Media
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RoomMediaDao : MediaDao, RoomEntityDao<Media> {

  @Query("SELECT * FROM media WHERE id IN (:ids)")
  abstract override fun getMediaByIds(ids: List<Long>): Flow<List<Media>>

  @Query("SELECT * FROM media WHERE tmdb_id = :id")
  abstract override suspend fun getMediaByTmdbId(id: Int): Media?

  @Query("SELECT * FROM media WHERE id = :id")
  abstract override fun getMediaByIdFlow(id: Long): Flow<Media>

  @Query("SELECT * FROM media WHERE id = :id")
  abstract override suspend fun getMediaWithId(id: Long): Media?

  @Query("SELECT tmdb_id FROM media WHERE id = :id")
  abstract override suspend fun getTmdbIdForMediaId(id: Long): Int?

  @Query("SELECT id FROM media WHERE tmdb_id = :tmdbId")
  abstract override suspend fun getIdForTmdbId(tmdbId: Int): Long?

  @Query("DELETE FROM media WHERE id = :id")
  abstract override suspend fun delete(id: Long)

  @Query("DELETE FROM media")
  abstract override suspend fun deleteAll()
}
