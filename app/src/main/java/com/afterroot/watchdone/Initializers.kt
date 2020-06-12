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

package com.afterroot.watchdone

import android.content.Context
import androidx.startup.Initializer
import com.afterroot.watchdone.di.apiModule
import com.afterroot.watchdone.di.appModule
import com.afterroot.watchdone.di.firebaseModule
import com.afterroot.watchdone.di.roomModule
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

class KoinInitializer : Initializer<KoinApplication> {
    override fun create(context: Context): KoinApplication = startKoin {
        androidLogger()
        androidContext(context)
        modules(listOf(firebaseModule, appModule, apiModule, roomModule))
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}

class FirestoreInitializer : Initializer<FirebaseFirestore> {
    override fun create(context: Context): FirebaseFirestore {
        return Firebase.firestore.apply {
            firestoreSettings =
                FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}