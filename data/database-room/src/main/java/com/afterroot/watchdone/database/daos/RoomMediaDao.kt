/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.database.daos

import androidx.room.Dao
import androidx.room.Query
import app.tivi.data.daos.RoomEntityDao
import com.afterroot.watchdone.data.daos.MediaDao
import com.afterroot.watchdone.data.model.Media
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RoomMediaDao :
  MediaDao,
  RoomEntityDao<Media> {

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
