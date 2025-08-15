/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.afterroot.watchdone.data.model.Genre
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {
  @Query("SELECT * from genres")
  fun getGenres(): LiveData<List<Genre>>

  @Query("SELECT * from genres WHERE id IN (:genres)")
  fun getGenresLiveData(genres: List<Int>): LiveData<List<Genre>>

  @Query("SELECT * from genres WHERE id IN (:genres)")
  fun getGenres(genres: List<Int>): Flow<List<Genre>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun add(vararg genre: Genre)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun add(genres: List<Genre>)

  @Delete
  suspend fun delete(genre: Genre)

  @Query("SELECT * from genres WHERE id LIKE :genreId")
  fun get(genreId: Int): Genre
}
