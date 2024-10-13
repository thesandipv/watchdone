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
