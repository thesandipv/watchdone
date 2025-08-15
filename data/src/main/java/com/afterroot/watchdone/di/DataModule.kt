/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.di

import com.afterroot.watchdone.data.repositories.UserDataRepository
import com.afterroot.watchdone.data.repositories.UserDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
  @Binds
  abstract fun bindsUserDataRepository(
    userDataRepository: UserDataRepositoryImpl,
  ): UserDataRepository
}
