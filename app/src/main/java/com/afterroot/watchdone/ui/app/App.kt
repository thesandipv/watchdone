/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.ui.app

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DashboardCustomize
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.navigation.ModalBottomSheetLayout
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.afterroot.ui.common.compose.components.LocalUsingFirebaseEmulators
import com.afterroot.ui.common.compose.navigation.RootScreen
import com.afterroot.ui.common.compose.theme.ubuntuTypography
import com.afterroot.watchdone.resources.R as CommonR

@Composable
fun App(
  appState: AppState,
  modifier: Modifier = Modifier,
  onWatchProviderClick: (link: String) -> Unit = { _ -> },
  settingsAction: () -> Unit = {},
  shareToIG: ((mediaId: Int, poster: String) -> Unit)? = null,
) {
  val snackbarHostState = remember { SnackbarHostState() }

  val isOffline by appState.isOffline.collectAsStateWithLifecycle()

  // If user is not connected to the internet show a snack bar to inform them.
  val notConnectedMessage = "Not Connected to internet"
  LaunchedEffect(isOffline) {
    if (isOffline) {
      snackbarHostState.showSnackbar(
        message = notConnectedMessage,
        duration = Indefinite,
      )
    }
  }

  App(
    appState = appState,
    snackbarHostState = snackbarHostState,
    modifier = modifier,
    onWatchProviderClick = onWatchProviderClick,
    settingsAction = settingsAction,
    shareToIG = shareToIG,
  )
}

@Composable
fun App(
  appState: AppState,
  snackbarHostState: SnackbarHostState,
  modifier: Modifier = Modifier,
  windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
  settingsAction: () -> Unit = {},
  onWatchProviderClick: (link: String) -> Unit = { _ -> },
  shareToIG: ((mediaId: Int, poster: String) -> Unit)? = null,
) {
  val currentSelectedItem by appState.navController.currentScreenAsState()

  WatchdoneNavigationSuiteScaffold(
    windowAdaptiveInfo = windowAdaptiveInfo,
    navigationSuiteItems = {
      homeNavigationItems.forEach { navigationItem ->
        item(
          selected = navigationItem.screen == currentSelectedItem,
          icon = {
            HomeNavigationItemIcon(
              item = navigationItem,
              selected = navigationItem.screen == currentSelectedItem,
            )
          },
          label = {
            Text(text = stringResource(id = navigationItem.labelResId))
          },
          onClick = {
            appState.navController.navigate(navigationItem.screen.route) {
              launchSingleTop = true
              restoreState = true

              popUpTo(appState.navController.graph.findStartDestination().id) {
                saveState = true
              }
            }
          },
        )
      }
    },
  ) {
    Scaffold(
      modifier = modifier,
      snackbarHost = { SnackbarHost(snackbarHostState) },
      bottomBar = {
        Column {
          AnimatedVisibility(visible = LocalUsingFirebaseEmulators.current) {
            ProvideTextStyle(
              value = ubuntuTypography.bodySmall.copy(
                textAlign = TextAlign.Center,
              ),
            ) {
              Text(
                text = "USING FIREBASE EMULATORS",
                modifier = Modifier
                  .fillMaxWidth()
                  .background(Color(0xFFFF6E40)),
              )
            }
          }
        }
      },
    ) { paddingValues ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .consumeWindowInsets(paddingValues)
          .windowInsetsPadding(WindowInsets.safeDrawing),
      ) {
        ModalBottomSheetLayout(bottomSheetNavigator = appState.bottomSheetNavigator) {
          AppNavigation(
            appState = appState,
            modifier = Modifier
              .weight(1f)
              .fillMaxHeight(),
            onWatchProviderClick = onWatchProviderClick,
            settingsAction = settingsAction,
            shareToIG = shareToIG,
          )
        }
      }
    }
  }
}

/**
 * Adds an [NavController.OnDestinationChangedListener] to this [NavController] and updates the
 * returned [State] which is updated as the destination changes.
 */
@Stable
@Composable
private fun NavController.currentScreenAsState(): State<RootScreen> {
  val selectedItem = remember { mutableStateOf<RootScreen>(RootScreen.Watchlist) }

  DisposableEffect(this) {
    val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
      when {
        destination.hierarchy.any { it.route == RootScreen.Watchlist.route } -> {
          selectedItem.value = RootScreen.Watchlist
        }

        destination.hierarchy.any { it.route == RootScreen.Discover.route } -> {
          selectedItem.value = RootScreen.Discover
        }

        destination.hierarchy.any { it.route == RootScreen.Search.route } -> {
          selectedItem.value = RootScreen.Search
        }

        destination.hierarchy.any { it.route == RootScreen.Profile.route } -> {
          selectedItem.value = RootScreen.Profile
        }
      }
    }
    addOnDestinationChangedListener(listener)

    onDispose {
      removeOnDestinationChangedListener(listener)
    }
  }

  return selectedItem
}

@Composable
fun HomeNavigationBar(
  selectedRootScreen: RootScreen,
  onNavigationSelected: (RootScreen) -> Unit,
  modifier: Modifier = Modifier,
) {
  NavigationBar(modifier = modifier) {
    for (item in homeNavigationItems) {
      NavigationBarItem(
        selected = selectedRootScreen == item.screen,
        onClick = { onNavigationSelected(item.screen) },
        label = { Text(text = stringResource(id = item.labelResId)) },
        icon = {
          HomeNavigationItemIcon(
            item = item,
            selected = selectedRootScreen == item.screen,
          )
        },
      )
    }
  }
}

private sealed class HomeNavigationItem(
  val screen: RootScreen,
  @StringRes val labelResId: Int,
  @StringRes val contentDescriptionResId: Int,
) {
  class ResourceIcon(
    screen: RootScreen,
    @StringRes labelResId: Int,
    @StringRes contentDescriptionResId: Int,
    @DrawableRes val iconResId: Int,
    @DrawableRes val selectedIconResId: Int? = null,
  ) : HomeNavigationItem(screen, labelResId, contentDescriptionResId)

  class ImageVectorIcon(
    screen: RootScreen,
    @StringRes labelResId: Int,
    @StringRes contentDescriptionResId: Int,
    val iconImageVector: ImageVector,
    val selectedImageVector: ImageVector? = null,
  ) : HomeNavigationItem(screen, labelResId, contentDescriptionResId)
}

private val homeNavigationItems = listOf(
  HomeNavigationItem.ImageVectorIcon(
    screen = RootScreen.Watchlist,
    labelResId = CommonR.string.title_watchlist,
    contentDescriptionResId = CommonR.string.title_watchlist,
    iconImageVector = Icons.Outlined.DashboardCustomize,
    selectedImageVector = Icons.Default.DashboardCustomize,
  ),
  HomeNavigationItem.ImageVectorIcon(
    screen = RootScreen.Discover,
    labelResId = CommonR.string.text_discover,
    contentDescriptionResId = CommonR.string.text_discover,
    iconImageVector = Icons.Outlined.RemoveRedEye,
    selectedImageVector = Icons.Default.RemoveRedEye,
  ),
  HomeNavigationItem.ImageVectorIcon(
    screen = RootScreen.Search,
    labelResId = CommonR.string.title_search,
    contentDescriptionResId = CommonR.string.title_search,
    iconImageVector = Icons.Outlined.Search,
    selectedImageVector = Icons.Default.Search,
  ),
  HomeNavigationItem.ImageVectorIcon(
    screen = RootScreen.Profile,
    labelResId = CommonR.string.title_profile,
    contentDescriptionResId = CommonR.string.title_profile,
    iconImageVector = Icons.Outlined.AccountCircle,
    selectedImageVector = Icons.Default.AccountCircle,
  ),

)

@Composable
private fun HomeNavigationItemIcon(item: HomeNavigationItem, selected: Boolean) {
  val painter = when (item) {
    is HomeNavigationItem.ResourceIcon -> painterResource(item.iconResId)
    is HomeNavigationItem.ImageVectorIcon -> rememberVectorPainter(item.iconImageVector)
  }
  val selectedPainter = when (item) {
    is HomeNavigationItem.ResourceIcon -> item.selectedIconResId?.let { painterResource(it) }
    is HomeNavigationItem.ImageVectorIcon -> item.selectedImageVector?.let {
      rememberVectorPainter(
        it,
      )
    }
  }

  if (selectedPainter != null) {
    Crossfade(targetState = selected, label = "SelectedIcon") {
      Icon(
        painter = if (it) selectedPainter else painter,
        contentDescription = stringResource(item.contentDescriptionResId),
      )
    }
  } else {
    Icon(
      painter = painter,
      contentDescription = stringResource(item.contentDescriptionResId),
    )
  }
}

@Composable
fun WatchdoneNavigationSuiteScaffold(
  navigationSuiteItems: NavigationSuiteScope.() -> Unit,
  modifier: Modifier = Modifier,
  windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
  content: @Composable () -> Unit,
) {
  val layoutType = NavigationSuiteScaffoldDefaults
    .calculateFromAdaptiveInfo(windowAdaptiveInfo)
  NavigationSuiteScaffold(
    navigationSuiteItems = navigationSuiteItems,
    layoutType = layoutType,
    modifier = modifier,
  ) {
    content()
  }
}
