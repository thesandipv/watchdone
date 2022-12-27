/*
 * Copyright (C) 2020-2022 Sandip Vaghela
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
import com.afterroot.watchdone.BuildConfig
import com.afterroot.watchdone.base.CoroutineDispatchers
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.media.adapter.CastListAdapter
import com.afterroot.watchdone.utils.getMailBodyForFeedback
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModules {
    @Singleton
    @Provides
    fun provideDispatchers() = CoroutineDispatchers(
        default = Dispatchers.Default,
        io = Dispatchers.IO,
        main = Dispatchers.Main
    )

    @Provides
    @Named("feedback_email")
    fun provideFeedbackEmail(): String = "afterhasroot@gmail.com"

    @Provides
    @Named("version_Code")
    fun provideVersionCode(): Int = BuildConfig.VERSION_CODE

    @Provides
    @Named("version_name")
    fun provideVersionName(): String = BuildConfig.VERSION_NAME

    @Provides
    @Named("version_string")
    fun provideVersionString() =
        "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) - ${BuildConfig.COMMIT_ID}"

    @Provides
    @Named("feedback_body")
    fun provideFeedbackBody(firebaseUtils: FirebaseUtils): String =
        getMailBodyForFeedback(firebaseUtils, version = provideVersionName(), versionCode = provideVersionCode())

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    fun provideCastListAdapter(settings: Settings): CastListAdapter = CastListAdapter(settings)
}
