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
import com.afterroot.watchdone.data.model.Country
import kotlinx.coroutines.flow.Flow

@Dao
interface CountriesDao {
  @Query("SELECT * from countries ORDER BY englishName ASC")
  fun getCountries(): LiveData<List<Country>>

  @Query("SELECT * from countries ORDER BY englishName ASC")
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
