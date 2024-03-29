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

package com.afterroot.watchdone.tmdb

import app.moviebase.tmdb.Tmdb3
import app.tivi.tmdb.TmdbOAuthInfo
import com.afterroot.watchdone.base.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.http.HttpStatusCode
import javax.inject.Named
import javax.inject.Singleton
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object TmdbModule {
    @Provides
    @Singleton
    fun provideTmdb3(
        @Named("tmdb") okHttpClient: OkHttpClient,
        tmdbOAuthInfo: TmdbOAuthInfo,
    ): Tmdb3 = Tmdb3 {
        tmdbApiKey = tmdbOAuthInfo.apiKey
        maxRetriesOnException = 3

        httpClient(OkHttp) {
            engine {
                preconfigured = okHttpClient
            }

            install(HttpRequestRetry) {
                retryIf(5) { _, httpResponse ->
                    when {
                        httpResponse.status.value in 500..599 -> true
                        httpResponse.status == HttpStatusCode.TooManyRequests -> true
                        else -> false
                    }
                }
            }
        }
    }

    @Provides
    @Singleton
    fun provideTmdbOAuthInfo() = TmdbOAuthInfo(apiKey = BuildConfig.TMDB_API)

    @Provides
    @Named("tmdb")
    fun provideOkHttpClient() = OkHttpClient().newBuilder().build()
}
