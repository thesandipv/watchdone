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
}
