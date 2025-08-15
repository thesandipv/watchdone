/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.discover

import com.afterroot.watchdone.di.Tmdb
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DiscoverDataSourceBinds {
  @Binds
  @Tmdb
  abstract fun bindDiscoverDataSource(
    tmdbDiscoverDataSource: TmdbDiscoverDataSource,
  ): DiscoverDataSource
}
