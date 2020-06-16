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

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.startup.AppInitializer
import com.afterroot.tmdbapi.TmdbApi
import com.afterroot.watchdone.BuildConfig
import com.afterroot.watchdone.FirestoreInitializer
import com.afterroot.watchdone.network.NetworkStateMonitor
import com.afterroot.watchdone.ui.settings.Settings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val firebaseModule = module {
    single {
        AppInitializer.getInstance(androidContext()).initializeComponent(FirestoreInitializer::class.java)
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

    single {
        androidContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
val networkModule = module {
    single {
        NetworkStateMonitor(get())
    }
}