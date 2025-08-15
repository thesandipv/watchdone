/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.di

import com.afterroot.watchdone.data.tmdb.account.TmdbAccountActions
import com.afterroot.watchdone.data.tmdb.account.TmdbAccountActionsImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AccountModule {

  @Binds
  abstract fun bindTmdbAccountActions(
    tmdbLoginActionImpl: TmdbAccountActionsImpl,
  ): TmdbAccountActions
}
