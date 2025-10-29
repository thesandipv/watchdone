/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
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
