/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.afterroot.watchdone.data.tmdb.auth.AuthStore
import com.afterroot.watchdone.data.tmdb.auth.TmdbAuthActions
import com.afterroot.watchdone.data.tmdb.auth.TmdbAuthActionsImpl
import com.afterroot.watchdone.data.tmdb.auth.WatchdoneAuthStore
import com.afterroot.watchdone.di.Tmdb
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

  @Binds
  abstract fun bindAuthStore(authStore: WatchdoneAuthStore): AuthStore

  @Binds
  abstract fun bindTmdbLoginAction(tmdbLoginActionImpl: TmdbAuthActionsImpl): TmdbAuthActions

  companion object {
    @Tmdb
    @Provides
    @Singleton
    fun provideAuthSharedPreferences(application: Application): SharedPreferences =
      application.getSharedPreferences("wd_tmdb_auth", Context.MODE_PRIVATE)
  }
}
