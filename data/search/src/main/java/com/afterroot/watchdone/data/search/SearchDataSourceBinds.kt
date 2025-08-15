/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.search

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchDataSourceBinds {
  @Binds
  abstract fun bindSearchMediaDataSource(
    tmdbSearchMediaDataSource: TmdbSearchMediaDataSource,
  ): SearchDataSource
}
