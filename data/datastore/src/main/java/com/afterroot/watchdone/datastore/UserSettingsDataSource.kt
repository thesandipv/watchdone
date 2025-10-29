/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.datastore

import androidx.datastore.core.DataStore
import app.tivi.util.Logger
import com.afterroot.watchdone.data.model.DarkThemeConfig
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.data.model.UserData
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.map

class UserSettingsDataSource @Inject constructor(
  private val userSettings: DataStore<UserSettings>,
  private val logger: Logger,
) {
  val data = userSettings.data.map {
    UserData(
      isFirstInstalled = it.isFirstInstalled,
      useDynamicColor = it.useDynamicColor,
      isUserSignedIn = false,
      darkThemeConfig = when (it.darkThemeConfig) {
        null,
        DarkThemeConfigProto.DARK_THEME_CONFIG_UNSPECIFIED,
        DarkThemeConfigProto.UNRECOGNIZED,
        DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM,
        -> DarkThemeConfig.FOLLOW_SYSTEM

        DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT -> DarkThemeConfig.LIGHT
        DarkThemeConfigProto.DARK_THEME_CONFIG_DARK -> DarkThemeConfig.DARK
      },
      tmdbBaseUrl = it.tmdbBaseUrl,
      tmdbPosterSizes = it.tmdbPosterSizesMap.keys,
      mediaTypeViews = it.mediaTypeViewsMap,
    )
  }

  suspend fun setUseDynamicColor(value: Boolean) = updateData { it.useDynamicColor = value }

  suspend fun setIsFirstInstalled(value: Boolean) = updateData { it.isFirstInstalled = value }

  suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) = updateData {
    it.darkThemeConfig = when (darkThemeConfig) {
      DarkThemeConfig.FOLLOW_SYSTEM -> DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
      DarkThemeConfig.LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
      DarkThemeConfig.DARK -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
    }
  }

  suspend fun setTMDbBaseUrl(baseUrl: String) = updateData { it.tmdbBaseUrl = baseUrl }

  suspend fun setPrefImageSize(imageSize: String) = updateData { it.prefImageSize = imageSize }

  suspend fun setTMDbPosterSizes(posterSizes: Set<String>) = try {
    userSettings.updateData {
      it.copy {
        tmdbPosterSizes.apply {
          clear()
          putAll(posterSizes.associateWith { true })
        }
      }
    }
  } catch (ioException: IOException) {
    logger.e(ioException) { "Failed to update settings" }
  }

  suspend fun updateMediaTypeViews(viewName: String, mediaType: MediaType) = try {
    userSettings.updateData {
      it.copy {
        mediaTypeViews.put(viewName, mediaType.name)
      }
    }
  } catch (ioException: IOException) {
    logger.e(ioException) { "Failed to update settings" }
  }

  suspend fun clearMediaTypeViews() = try {
    userSettings.updateData {
      it.copy {
        mediaTypeViews.clear()
      }
    }
  } catch (ioException: IOException) {
    logger.e(ioException) { "Failed to update settings" }
  }

  private suspend fun updateData(block: (UserSettingsKt.Dsl) -> Unit) = try {
    userSettings.updateData {
      it.copy {
        block(this)
      }
    }
  } catch (ioException: IOException) {
    logger.e(ioException) { "Failed to update settings" }
  }
}
