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
