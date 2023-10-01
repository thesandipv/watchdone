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

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.afterroot.tmdbapi.model.config.Country
import kotlinx.coroutines.flow.Flow

@Dao
interface CountriesDao {
    @Query("SELECT * from countries")
    fun getCountries(): LiveData<List<Country>>

    @Query("SELECT * from countries")
    fun getCountriesFlow(): Flow<List<Country>>

    @Query("SELECT * from countries WHERE iso LIKE :iso")
    fun get(iso: String): Flow<Country?>

    @Query("SELECT * from countries WHERE englishName LIKE :name")
    fun getByName(name: String): Flow<Country?>

    @Delete
    suspend fun delete(country: Country)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(vararg countries: Country)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(countries: List<Country>)

    @Query("SELECT COUNT(iso) from countries")
    fun count(): Int
}
