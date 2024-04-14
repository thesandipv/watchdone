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

import com.afterroot.data.utils.FirebaseUtils
import com.afterroot.watchdone.base.CoroutineDispatchers
import com.afterroot.watchdone.utils.getMailBodyForFeedback
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object AppModules {
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
  @Named("feedback_email")
  fun provideFeedbackEmail(): String = "afterhasroot@gmail.com"

  @Provides
  @Named("feedback_body")
  fun provideFeedbackBody(
    firebaseUtils: FirebaseUtils,
    @VersionName version: String,
    @VersionCode versionCode: Int,
  ): String =
    getMailBodyForFeedback(
      firebaseUtils,
      version = version,
      versionCode = versionCode,
    )

  @Provides
  @Singleton
  fun provideGson(): Gson = GsonBuilder().create()
}
