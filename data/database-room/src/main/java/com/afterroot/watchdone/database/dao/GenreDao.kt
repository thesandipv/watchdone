/*
 * Copyright (C) 2020-2024 Sandip Vaghela
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
