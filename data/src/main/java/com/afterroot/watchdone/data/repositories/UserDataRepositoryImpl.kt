/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.repositories

import com.afterroot.watchdone.data.model.DarkThemeConfig
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.data.model.UserData
import com.afterroot.watchdone.datastore.UserSettingsDataSource
import com.afterroot.watchdone.settings.Settings
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class UserDataRepositoryImpl @Inject constructor(
  val settings: Settings,
  private val userSettingsDataSource: UserSettingsDataSource,
) : UserDataRepository {
  override val userData: Flow<UserData>
    get() = userSettingsDataSource.data

  override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
    userSettingsDataSource.setDarkThemeConfig(darkThemeConfig)
  }

  override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
    userSettingsDataSource.setUseDynamicColor(useDynamicColor)
  }

  override suspend fun setIsFirstInstalled(value: Boolean) {
    userSettingsDataSource.setIsFirstInstalled(value)
  }

  override suspend fun setTMDbBaseUrl(baseUrl: String) {
    userSettingsDataSource.setTMDbBaseUrl(baseUrl)
  }

  override suspend fun setPrefImageSize(imageSize: String) {
    userSettingsDataSource.setPrefImageSize(imageSize)
  }

  override suspend fun setTMDbPosterSizes(posterSizes: Set<String>) {
    userSettingsDataSource.setTMDbPosterSizes(posterSizes)
  }

  override suspend fun updateMediaTypeViews(viewName: String, mediaType: MediaType) {
    userSettingsDataSource.updateMediaTypeViews(viewName, mediaType)
  }

  override suspend fun clearMediaTypeViews() {
    userSettingsDataSource.clearMediaTypeViews()
  }
}
