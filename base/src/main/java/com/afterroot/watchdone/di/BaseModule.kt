/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.di

import com.afterroot.watchdone.base.CoroutineDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@InstallIn(SingletonComponent::class)
@Module
object BaseModule {

  @Singleton
  @Provides
  fun provideDispatchers() = CoroutineDispatchers(
    default = Dispatchers.Default,
    io = Dispatchers.IO,
    main = Dispatchers.Main,
    databaseWrite = Dispatchers.IO.limitedParallelism(1),
    databaseRead = Dispatchers.IO.limitedParallelism(4),
  )

  @Provides
  fun provideApplicationCoroutineScope(dispatchers: CoroutineDispatchers): CoroutineScope =
    CoroutineScope(dispatchers.main + SupervisorJob())
}
