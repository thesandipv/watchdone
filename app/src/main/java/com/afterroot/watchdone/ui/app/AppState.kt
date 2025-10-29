/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.ui.app

import androidx.compose.material.navigation.BottomSheetNavigator
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import com.afterroot.watchdone.utils.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

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

@Composable
fun rememberAppState(
  navController: NavHostController,
  bottomSheetNavigator: BottomSheetNavigator,
  windowSizeClass: WindowSizeClass,
  networkMonitor: NetworkMonitor,
  coroutineScope: CoroutineScope = rememberCoroutineScope(),
): AppState = remember(
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
