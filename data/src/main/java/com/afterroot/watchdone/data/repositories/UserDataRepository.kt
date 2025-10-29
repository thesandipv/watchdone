/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.repositories

import com.afterroot.watchdone.data.model.DarkThemeConfig
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.data.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
  val userData: Flow<UserData>
  suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)
  suspend fun setDynamicColorPreference(useDynamicColor: Boolean)
  suspend fun setIsFirstInstalled(value: Boolean)
  suspend fun setTMDbBaseUrl(baseUrl: String)
  suspend fun setPrefImageSize(imageSize: String)
  suspend fun setTMDbPosterSizes(posterSizes: Set<String>)
  suspend fun updateMediaTypeViews(viewName: String, mediaType: MediaType)
  suspend fun clearMediaTypeViews()
}
