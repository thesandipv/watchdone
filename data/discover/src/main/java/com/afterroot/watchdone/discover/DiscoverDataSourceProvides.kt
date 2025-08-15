/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.discover

import app.moviebase.tmdb.model.TmdbDiscover
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DiscoverDataSourceProvides {
  @Provides
  fun provideTmdbDiscoverMovie() = TmdbDiscover.Movie(
    includeAdult = true,
  )

  @Provides
  fun provideTmdbDiscoverShow() = TmdbDiscover.Show()
}
