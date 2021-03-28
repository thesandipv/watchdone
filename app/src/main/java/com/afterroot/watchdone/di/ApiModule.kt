/*
 * Copyright (C) 2020-2021 Sandip Vaghela
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

import com.afterroot.tmdbapi2.Constants
import com.afterroot.tmdbapi2.TMDbInterceptor
import com.afterroot.tmdbapi2.api.AuthApi
import com.afterroot.tmdbapi2.api.ConfigApi
import com.afterroot.tmdbapi2.api.DiscoverApi
import com.afterroot.tmdbapi2.api.GenresApi
import com.afterroot.tmdbapi2.api.MoviesApi
import com.afterroot.tmdbapi2.api.SearchApi
import com.afterroot.tmdbapi2.api.TVApi
import com.afterroot.tmdbapi2.repository.AuthRepository
import com.afterroot.tmdbapi2.repository.ConfigRepository
import com.afterroot.tmdbapi2.repository.DiscoverRepository
import com.afterroot.tmdbapi2.repository.GenresRepository
import com.afterroot.tmdbapi2.repository.MoviesRepository
import com.afterroot.tmdbapi2.repository.SearchRepository
import com.afterroot.tmdbapi2.repository.TVRepository
import com.afterroot.watchdone.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

val apiModule = module {
    factory { provideOkHttpClient() }
    single { provideRetrofit(get()) }

    factory { provideMoviesApi(get()) }
    factory { MoviesRepository(get()) }

    factory { provideAuthApi(get()) }
    factory { AuthRepository(get()) }

    factory { provideDiscoverApi(get()) }
    factory { DiscoverRepository(get()) }

    factory { provideGenresApi(get()) }
    factory { GenresRepository(get()) }

    factory { provideConfigApi(get()) }
    factory { ConfigRepository(get()) }

    factory { provideTVApi(get()) }
    factory { TVRepository(get()) }

    factory { provideSearchApi(get()) }
    factory { SearchRepository(get()) }
}

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(Constants.TMDB_BASE_URL)
        .addConverterFactory(JacksonConverterFactory.create())
        .client(okHttpClient)
        .build()
}

fun provideOkHttpClient() = OkHttpClient().newBuilder()
    .addInterceptor(TMDbInterceptor(BuildConfig.TMDB_API, v4ApiKey = BuildConfig.TMDB_BEARER_TOKEN))
    .addInterceptor(
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
    ).build()

fun provideMoviesApi(retrofit: Retrofit): MoviesApi = retrofit.create(MoviesApi::class.java)
fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)
fun provideDiscoverApi(retrofit: Retrofit): DiscoverApi = retrofit.create(DiscoverApi::class.java)
fun provideGenresApi(retrofit: Retrofit): GenresApi = retrofit.create(GenresApi::class.java)
fun provideConfigApi(retrofit: Retrofit): ConfigApi = retrofit.create(ConfigApi::class.java)
fun provideTVApi(retrofit: Retrofit): TVApi = retrofit.create(TVApi::class.java)
fun provideSearchApi(retrofit: Retrofit): SearchApi = retrofit.create(SearchApi::class.java)
