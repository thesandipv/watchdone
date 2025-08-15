/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.di

import com.afterroot.data.utils.FirebaseUtils
import com.afterroot.watchdone.utils.getMailBodyForFeedback
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModules {

  @Provides
  @Named("feedback_email")
  fun provideFeedbackEmail(): String = "afterhasroot@gmail.com"

  @Provides
  @Named("feedback_body")
  fun provideFeedbackBody(
    firebaseUtils: FirebaseUtils,
    @VersionName version: String,
    @VersionCode versionCode: Int,
  ): String = getMailBodyForFeedback(
    firebaseUtils,
    version = version,
    versionCode = versionCode,
  )

  @Provides
  @Singleton
  fun provideGson(): Gson = GsonBuilder().create()
}
