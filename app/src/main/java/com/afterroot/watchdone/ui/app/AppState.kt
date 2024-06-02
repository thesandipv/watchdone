/*
 * Copyright (C) 2020-2024 Sandip Vaghela
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

package com.afterroot.watchdone.ui.app

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import com.afterroot.watchdone.utils.NetworkMonitor
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalMaterialNavigationApi::class)
@Stable
class AppState(
  val navController: NavHostController,
  val bottomSheetNavigator: BottomSheetNavigator,
  val windowSizeClass: WindowSizeClass,
  networkMonitor: NetworkMonitor,
  coroutineScope: CoroutineScope,
) {
  val shouldShowBottomBar: Boolean
    get() = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

  val shouldShowNavRail: Boolean
    get() = !shouldShowBottomBar

  val isOffline = networkMonitor.isOnline
    .map(Boolean::not)
    .stateIn(
      scope = coroutineScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = false,
    )
}

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun rememberAppState(
  navController: NavHostController,
  bottomSheetNavigator: BottomSheetNavigator,
  windowSizeClass: WindowSizeClass,
  networkMonitor: NetworkMonitor,
  coroutineScope: CoroutineScope = rememberCoroutineScope(),
): AppState {
  return remember(
    navController,
    bottomSheetNavigator,
    windowSizeClass,
    networkMonitor,
    coroutineScope,
  ) {
    AppState(
      navController = navController,
      bottomSheetNavigator = bottomSheetNavigator,
      windowSizeClass = windowSizeClass,
      networkMonitor = networkMonitor,
      coroutineScope = coroutineScope,
    )
  }
}
