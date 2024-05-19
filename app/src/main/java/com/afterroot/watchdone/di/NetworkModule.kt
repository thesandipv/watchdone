/*
 * Copyright (C) 2020-2023 Sandip Vaghela
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

import android.content.Context
import android.net.ConnectivityManager
import com.afterroot.utils.network.NetworkStateMonitor
import com.afterroot.watchdone.utils.ConnectivityManagerNetworkMonitor
import com.afterroot.watchdone.utils.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  @Provides
  @Singleton
  fun provideCM(@ApplicationContext context: Context): ConnectivityManager =
    context.applicationContext.getSystemService(
      Context.CONNECTIVITY_SERVICE,
    ) as ConnectivityManager

  @Provides
  @Singleton
  fun provideStateMonitor(
    connectivityManager: ConnectivityManager,
  ) = NetworkStateMonitor(connectivityManager)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModuleBinds {
  @Binds
  abstract fun bindsNetworkMonitor(
    networkMonitor: ConnectivityManagerNetworkMonitor,
  ): NetworkMonitor
}
