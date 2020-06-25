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
import android.os.Build
import androidx.annotation.Keep
import androidx.startup.Initializer
import com.afterroot.watchdone.di.apiModule
import com.afterroot.watchdone.di.appModule
import com.afterroot.watchdone.di.firebaseModule
import com.afterroot.watchdone.di.networkModule
import com.afterroot.watchdone.di.roomModule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

@Keep
class KoinInitializer : Initializer<KoinApplication> {
    override fun create(context: Context): KoinApplication = startKoin {
        androidLogger()
        androidContext(context)
        val modulesList = mutableListOf(firebaseModule, appModule, apiModule, roomModule)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            modulesList.add(networkModule)
        }
        modules(modulesList)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}

class FirestoreInitializer : Initializer<FirebaseFirestore> {
    override fun create(context: Context): FirebaseFirestore = Firebase.firestore.apply {
        firestoreSettings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}

class AuthInitializer : Initializer<FirebaseAuth> {
    override fun create(context: Context): FirebaseAuth = FirebaseAuth.getInstance()
    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}