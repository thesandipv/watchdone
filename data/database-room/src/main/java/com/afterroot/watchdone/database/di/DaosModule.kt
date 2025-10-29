/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.database.di

import com.afterroot.watchdone.data.daos.DiscoverDao
import com.afterroot.watchdone.data.daos.MediaDao
import com.afterroot.watchdone.data.daos.RecommendedDao
import com.afterroot.watchdone.database.WatchdoneDatabase
import com.afterroot.watchdone.database.dao.CountriesDao
import com.afterroot.watchdone.database.dao.GenreDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {
  @Provides
  fun provideMediaDao(database: WatchdoneDatabase): MediaDao = database.mediaDao()

  @Provides
  fun provideGenreDao(database: WatchdoneDatabase): GenreDao = database.genreDao()

  @Provides
  fun provideCountriesDao(database: WatchdoneDatabase): CountriesDao = database.countriesDao()

  @Provides
  fun provideDiscoverDao(database: WatchdoneDatabase): DiscoverDao = database.discoverDao()

  @Provides
  fun provideRecommendedDao(database: WatchdoneDatabase): RecommendedDao = database
    .recommendedDao()
}
