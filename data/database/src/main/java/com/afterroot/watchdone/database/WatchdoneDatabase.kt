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

package com.afterroot.watchdone.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.afterroot.tmdbapi.model.Genre
import com.afterroot.tmdbapi.model.config.Country
import com.afterroot.watchdone.data.daos.CountriesDao
import com.afterroot.watchdone.data.daos.GenreDao

@Database(
    entities = [Genre::class, Country::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ],
)
abstract class WatchdoneDatabase : RoomDatabase() {
    abstract fun genreDao(): GenreDao
    abstract fun countriesDao(): CountriesDao
}
