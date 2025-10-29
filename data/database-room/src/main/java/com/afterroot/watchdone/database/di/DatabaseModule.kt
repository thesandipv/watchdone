/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.database.di

import android.content.Context
import androidx.room.Room
import app.tivi.data.db.DatabaseTransactionRunner
import app.tivi.data.db.RoomTransactionRunner
import com.afterroot.watchdone.database.WatchdoneDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
  @Provides
  @Singleton
  fun provideWatchdoneDatabase(@ApplicationContext context: Context): WatchdoneDatabase =
    Room.databaseBuilder(
      context,
      WatchdoneDatabase::class.java,
      "watchdone-db",
    ).build()

  @Provides
  @Singleton
  fun provideDatabaseTransactionRunner(runner: RoomTransactionRunner): DatabaseTransactionRunner =
    runner
}
