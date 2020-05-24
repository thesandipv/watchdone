/*
 * Copyright (C) 2020 Sandip Vaghela
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

import com.afterroot.tmdbapi.TmdbApi
import com.afterroot.tmdbapi2.Constants.TMDB_BASE_URL
import com.afterroot.tmdbapi2.TMDbInterceptor
import com.afterroot.tmdbapi2.api.AuthApi
import com.afterroot.tmdbapi2.api.MoviesApi
import com.afterroot.tmdbapi2.repository.AuthRepository
import com.afterroot.tmdbapi2.repository.MoviesRepository
import com.afterroot.watchdone.BuildConfig
import com.afterroot.watchdone.ui.settings.Settings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

val firebaseModule = module {
    single {
        Firebase.firestore.apply {
            firestoreSettings =
                FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()
        }
    }

    single {
        FirebaseStorage.getInstance()
    }

    single {
        FirebaseAuth.getInstance()
    }
}

val appModule = module {
    single {
        Settings(androidContext())
    }

    single {
        TmdbApi(BuildConfig.TMDB_API)
    }
}

val apiModule = module {
    factory { provideOkHttpClient() }
    single { provideRetrofit(get()) }

    factory { provideMoviesApi(get()) }
    factory { MoviesRepository(get()) }

    factory { provideAuthProvider(get()) }
    factory { AuthRepository(get()) }
}

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(TMDB_BASE_URL)
        .addConverterFactory(JacksonConverterFactory.create())
        .client(okHttpClient)
        .build()
}

fun provideOkHttpClient() = OkHttpClient().newBuilder()
    .addInterceptor(TMDbInterceptor(BuildConfig.TMDB_API))
    .addInterceptor(HttpLoggingInterceptor().apply {
        level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }).build()

fun provideMoviesApi(retrofit: Retrofit): MoviesApi = retrofit.create(MoviesApi::class.java)
fun provideAuthProvider(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)