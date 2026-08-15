/*
 * Copyright (C) 2020-2026 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.core.testing

import app.moviebase.tmdb.Tmdb3
import app.tivi.tmdb.TmdbOAuthInfo
import com.afterroot.watchdone.base.BuildConfig
import com.afterroot.watchdone.data.tmdb.auth.TmdbAuthRepository
import com.afterroot.watchdone.tmdb.TmdbModule
import com.afterroot.watchdone.tmdb.TmdbOkHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.http.HttpStatusCode
import javax.inject.Singleton
import okhttp3.OkHttpClient

@Module
@TestInstallIn(
  components = [SingletonComponent::class],
  replaces = [TmdbModule::class],
)
object TestTmdbModule {
  @Provides
  @Singleton
  fun provideTmdb3(
    @TmdbOkHttpClient okHttpClient: OkHttpClient,
    tmdbOAuthInfo: TmdbOAuthInfo,
    tmdbAuthRepository: TmdbAuthRepository,
  ): Tmdb3 = Tmdb3 {
    tmdbApiKey = tmdbOAuthInfo.apiKey

    httpClient(OkHttp) {
      engine {
        preconfigured = okHttpClient
      }

      install(HttpTimeout) {
        requestTimeoutMillis = 30000
        connectTimeoutMillis = 30000
        socketTimeoutMillis = 30000
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

    userAuthentication {
      loadSessionId {
        tmdbAuthRepository.getAuthState()?.sessionId
      }
    }
  }

  @Provides
  @Singleton
  fun provideTmdbOAuthInfo() = TmdbOAuthInfo(apiKey = BuildConfig.TMDB_API)

  @Provides
  @Singleton
  @TmdbOkHttpClient
  fun provideTmdbOkhttpClient(): OkHttpClient = OkHttpClient().newBuilder().build()
}
