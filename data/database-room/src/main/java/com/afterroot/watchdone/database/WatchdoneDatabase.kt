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

package com.afterroot.watchdone.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.afterroot.watchdone.data.model.Country
import com.afterroot.watchdone.data.model.DiscoverEntry
import com.afterroot.watchdone.data.model.Genre
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.model.RecommendedEntry
import com.afterroot.watchdone.database.dao.CountriesDao
import com.afterroot.watchdone.database.dao.GenreDao
import com.afterroot.watchdone.database.daos.RoomDiscoverDao
import com.afterroot.watchdone.database.daos.RoomMediaDao
import com.afterroot.watchdone.database.daos.RoomRecommendedDao
import com.afterroot.watchdone.database.migrations.MigrateFrom2to3
import com.afterroot.watchdone.database.util.InstantConverter

@Database(
  entities = [
    Genre::class,
    Country::class,
    Media::class,
    DiscoverEntry::class,
    RecommendedEntry::class,
  ],
  version = 5,
  autoMigrations = [
    AutoMigration(from = 1, to = 2),
    AutoMigration(from = 2, to = 3, spec = MigrateFrom2to3::class),
    AutoMigration(from = 3, to = 4),
    AutoMigration(from = 4, to = 5),
  ],
)
@TypeConverters(InstantConverter::class)
abstract class WatchdoneDatabase : RoomDatabase() {
  abstract fun genreDao(): GenreDao
  abstract fun countriesDao(): CountriesDao
  abstract fun mediaDao(): RoomMediaDao
  abstract fun discoverDao(): RoomDiscoverDao
  abstract fun recommendedDao(): RoomRecommendedDao
}
