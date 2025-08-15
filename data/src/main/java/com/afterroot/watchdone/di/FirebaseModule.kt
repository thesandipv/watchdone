/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.di

import com.afterroot.data.utils.FirebaseUtils
import com.afterroot.watchdone.utils.whenBuildIs
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
  @Provides
  @Singleton
  fun provideFirestore(): FirebaseFirestore = Firebase.firestore.apply {
    firestoreSettings = firestoreSettings {
      setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
    }
  }

  @Provides
  @Singleton
  fun provideAuth(): FirebaseAuth = Firebase.auth

  @Provides
  @Singleton
  fun provideRemoteConfig(): FirebaseRemoteConfig = Firebase.remoteConfig.apply {
    setConfigSettingsAsync(
      remoteConfigSettings {
        fetchTimeoutInSeconds = whenBuildIs(debug = 0, release = 3600)
      },
    )
  }

  @Provides
  @Singleton
  fun provideFirebaseUtils(firebaseAuth: FirebaseAuth) = FirebaseUtils(firebaseAuth)

  @Provides
  @Singleton
  fun provideFirebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()

  @Provides
  @Singleton
  fun provideFirebaseCrashlytics(): FirebaseCrashlytics = FirebaseCrashlytics.getInstance()
}
