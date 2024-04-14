/*
 * Copyright (C) 2020-2021 Sandip Vaghela
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
package com.afterroot.ui.common.compose.components

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import app.tivi.util.Logger
import com.afterroot.watchdone.data.model.LocalUser
import com.afterroot.watchdone.settings.Settings

val LocalCurrentUser = compositionLocalOf { LocalUser() }

val LocalTMDbBaseUrl = compositionLocalOf { "https://image.tmdb.org/t/p/" }
val LocalPosterSize = compositionLocalOf { "w342" }
val LocalBackdropSize = compositionLocalOf { "w780" }
val LocalLogoSize = compositionLocalOf { "w92" }
val LocalRegion = compositionLocalOf { "IN" }
val LocalSettings = staticCompositionLocalOf<Settings> {
  error("LocalSettings is not initialized")
}
val LocalLogger = staticCompositionLocalOf<Logger> {
  error("LocalLogger is not initialized")
}
val LocalWindowSizeClass = staticCompositionLocalOf<WindowSizeClass> {
  error("WindowSizeClass is not initialized")
}
