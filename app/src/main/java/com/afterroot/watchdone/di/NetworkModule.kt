/*
 * 2021 AfterROOT
 */
package com.afterroot.watchdone.di

import android.content.Context
import android.net.ConnectivityManager
import com.afterroot.core.network.NetworkStateMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideCM(@ApplicationContext context: Context): ConnectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides
    @Singleton
    fun provideStateMonitor(connectivityManager: ConnectivityManager) = NetworkStateMonitor(connectivityManager)
}
