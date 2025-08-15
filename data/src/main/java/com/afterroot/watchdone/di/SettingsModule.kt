/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.di

import android.content.Context
import android.content.SharedPreferences
import app.tivi.util.Logger
import com.afterroot.watchdone.settings.Settings
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SettingsModule {
  @Provides
  @Singleton
  fun provideSettings(
    @ApplicationContext context: Context,
    gson: Gson,
    logger: Logger,
    @Tmdb tmdbPrefs: SharedPreferences,
  ) = Settings(context, gson, logger, tmdbPrefs)
}
