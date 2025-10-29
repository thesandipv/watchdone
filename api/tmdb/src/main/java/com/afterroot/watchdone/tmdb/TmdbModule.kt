/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.tmdb

import app.moviebase.tmdb.Tmdb3
import app.tivi.tmdb.TmdbOAuthInfo
import com.afterroot.watchdone.base.BuildConfig
import com.afterroot.watchdone.data.tmdb.auth.TmdbAuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.http.HttpStatusCode
import javax.inject.Qualifier
import javax.inject.Singleton
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps

/**
 * OkHttpClient with Cloudflare DNS
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class TmdbOkHttpClient

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
private annotation class BaseOkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object TmdbModule {
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
  @BaseOkHttpClient
  fun provideOkHttpClient() = OkHttpClient().newBuilder().build()

  @Provides
  @Singleton
  @TmdbOkHttpClient
  fun provideTmdbOkhttpClient(@BaseOkHttpClient baseClient: OkHttpClient): OkHttpClient {
    val doh = DnsOverHttps.Builder()
      .client(baseClient)
      .url("https://1.1.1.1/dns-query".toHttpUrl())
      .build()

    return baseClient.newBuilder().dns(doh).build()
  }
}
