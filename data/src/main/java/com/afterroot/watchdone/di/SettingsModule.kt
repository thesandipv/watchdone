/*
 * 2021 AfterROOT
 */
package com.afterroot.watchdone.di

import android.content.Context
import com.afterroot.watchdone.settings.Settings
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SettingsModule {
    @Provides
    @Singleton
    fun provideSettings(@ApplicationContext context: Context, gson: Gson) = Settings(context, gson)
}
