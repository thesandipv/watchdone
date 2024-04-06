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
  fun provideRecommendedDao(
    database: WatchdoneDatabase,
  ): RecommendedDao = database
    .recommendedDao()
}
