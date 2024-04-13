/*
 * Copyright (C) 2020-2023 Sandip Vaghela
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
