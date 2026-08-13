/*
 * Copyright (C) 2020-2026 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.core.testing

import com.afterroot.data.utils.FirebaseUtils
import com.afterroot.watchdone.di.FirebaseModule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk
import javax.inject.Singleton

@Module
@TestInstallIn(
  components = [SingletonComponent::class],
  replaces = [FirebaseModule::class],
)
object TestFirebaseModule {
  @Provides
  @Singleton
  fun provideFirestore(): FirebaseFirestore = mockk(relaxed = true)

  @Provides
  @Singleton
  fun provideAuth(): FirebaseAuth = mockk(relaxed = true)

  @Provides
  @Singleton
  fun provideRemoteConfig(): FirebaseRemoteConfig = mockk(relaxed = true)

  @Provides
  @Singleton
  fun provideFirebaseUtils(firebaseAuth: FirebaseAuth) = FirebaseUtils(firebaseAuth)

  @Provides
  @Singleton
  fun provideFirebaseMessaging(): FirebaseMessaging = mockk(relaxed = true)

  @Provides
  @Singleton
  fun provideFirebaseCrashlytics(): FirebaseCrashlytics = mockk(relaxed = true)
}
