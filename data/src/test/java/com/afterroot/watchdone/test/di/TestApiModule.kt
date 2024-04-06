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
package com.afterroot.watchdone.test.di

import com.afterroot.tmdbapi.Constants
import com.afterroot.tmdbapi.TMDbInterceptor
import com.afterroot.tmdbapi.api.AuthApi
import com.afterroot.tmdbapi.api.ConfigApi
import com.afterroot.tmdbapi.api.DiscoverApi
import com.afterroot.tmdbapi.api.GenresApi
import com.afterroot.tmdbapi.api.MoviesApi
import com.afterroot.tmdbapi.api.SearchApi
import com.afterroot.tmdbapi.api.TVApi
import com.afterroot.tmdbapi.repository.AuthRepository
import com.afterroot.tmdbapi.repository.ConfigRepository
import com.afterroot.tmdbapi.repository.GenresRepository
import com.afterroot.tmdbapi.repository.MoviesRepository
import com.afterroot.tmdbapi.repository.SearchRepository
import com.afterroot.tmdbapi.repository.TVRepository
import com.afterroot.watchdone.data.BuildConfig
import com.afterroot.watchdone.utils.whenBuildIs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import info.movito.themoviedbapi.TmdbApi
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

@InstallIn(SingletonComponent::class)
@Module
object TestApiModule {
  @Provides
  @Singleton
  fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .baseUrl(Constants.TMDB_BASE_URL)
      .addConverterFactory(JacksonConverterFactory.create())
      .client(okHttpClient)
      .build()
  }

  @Provides
  fun provideOkHttpClient(
    tmdbInterceptor: TMDbInterceptor,
    httpLoggingInterceptor: HttpLoggingInterceptor,
  ) = OkHttpClient().newBuilder()
    .addInterceptor(tmdbInterceptor)
    .addInterceptor(httpLoggingInterceptor)
    .build()

  @Provides
  @Singleton
  fun provideTMDbInterceptor(): TMDbInterceptor =
    TMDbInterceptor(key = BuildConfig.TMDB_API, v4ApiKey = BuildConfig.TMDB_BEARER_TOKEN)

  @Provides
  @Singleton
  fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
    level = whenBuildIs(
      debug = HttpLoggingInterceptor.Level.BODY,
      release = HttpLoggingInterceptor.Level.NONE,
    )
  }

  @Provides
  @Singleton
  fun provideTmdbApi(
    okHttpClient: OkHttpClient,
  ): TmdbApi = TmdbApi(BuildConfig.TMDB_API, okHttpClient)
}

@Module
@InstallIn(SingletonComponent::class)
object TestRepositoriesModule {
  @Provides
  @Singleton
  fun provideMoviesRepository(moviesApi: MoviesApi) = MoviesRepository(moviesApi)

  @Provides
  @Singleton
  fun provideAuthRepository(authApi: AuthApi) = AuthRepository(authApi)

  @Provides
  @Singleton
  fun provideGenresRepository(genresApi: GenresApi) = GenresRepository(genresApi)

  @Provides
  @Singleton
  fun provideConfigRepository(configApi: ConfigApi) = ConfigRepository(configApi)

  @Provides
  @Singleton
  fun provideTVRepository(tvApi: TVApi) = TVRepository(tvApi)

  @Provides
  @Singleton
  fun provideSearchRepository(searchApi: SearchApi) = SearchRepository(searchApi)
}

@Module
@InstallIn(SingletonComponent::class)
object RetrofitApisModule {
  @Provides
  @Singleton
  fun provideMoviesApi(retrofit: Retrofit): MoviesApi = retrofit.create(MoviesApi::class.java)

  @Provides
  @Singleton
  fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

  @Provides
  @Singleton
  fun provideDiscoverApi(retrofit: Retrofit): DiscoverApi =
    retrofit.create(DiscoverApi::class.java)

  @Provides
  @Singleton
  fun provideGenresApi(retrofit: Retrofit): GenresApi = retrofit.create(GenresApi::class.java)

  @Provides
  @Singleton
  fun provideConfigApi(retrofit: Retrofit): ConfigApi = retrofit.create(ConfigApi::class.java)

  @Provides
  @Singleton
  fun provideTVApi(retrofit: Retrofit): TVApi = retrofit.create(TVApi::class.java)
}
