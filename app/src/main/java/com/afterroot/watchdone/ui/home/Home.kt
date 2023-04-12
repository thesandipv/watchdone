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

package com.afterroot.watchdone.ui.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DashboardCustomize
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.afterroot.ui.common.compose.navigation.RootScreen
import com.afterroot.watchdone.ui.navigation.AppNavigation
import com.afterroot.watchdone.resources.R as CommonR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(onWatchProviderClick: (link: String) -> Unit = { _ -> }) {
    val navController = rememberNavController()

    Scaffold(bottomBar = {
        val currentSelectedItem by navController.currentScreenAsState()
        HomeNavigationBar(
            selectedRootScreen = currentSelectedItem, onNavigationSelected = { seleted ->
                navController.navigate(seleted.route) {
                    launchSingleTop = true
                    restoreState = true

                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }) { paddingValues ->
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(paddingValues),
            onWatchProviderClick = onWatchProviderClick
        )
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
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        for (item in homeNavigationItems) {
            NavigationBarItem(
                selected = selectedRootScreen == item.screen,
                onClick = { onNavigationSelected(item.screen) },
                label = { Text(text = stringResource(id = item.labelResId)) },
                icon = { HomeNavigationItemIcon(item = item, selected = selectedRootScreen == item.screen) }
            )
        }
    }
}

private sealed class HomeNavigationItem(
    val screen: RootScreen,
    @StringRes val labelResId: Int,
    @StringRes val contentDescriptionResId: Int
) {
    class ResourceIcon(
        screen: RootScreen,
        @StringRes labelResId: Int,
        @StringRes contentDescriptionResId: Int,
        @DrawableRes val iconResId: Int,
        @DrawableRes val selectedIconResId: Int? = null
    ) : HomeNavigationItem(screen, labelResId, contentDescriptionResId)

    class ImageVectorIcon(
        screen: RootScreen,
        @StringRes labelResId: Int,
        @StringRes contentDescriptionResId: Int,
        val iconImageVector: ImageVector,
        val selectedImageVector: ImageVector? = null
    ) : HomeNavigationItem(screen, labelResId, contentDescriptionResId)
}

private val homeNavigationItems = listOf(
    HomeNavigationItem.ImageVectorIcon(
        screen = RootScreen.Watchlist,
        labelResId = CommonR.string.title_watchlist,
        contentDescriptionResId = CommonR.string.title_watchlist,
        iconImageVector = Icons.Outlined.DashboardCustomize,
        selectedImageVector = Icons.Default.DashboardCustomize
    ),
    HomeNavigationItem.ImageVectorIcon(
        screen = RootScreen.Discover,
        labelResId = CommonR.string.text_discover,
        contentDescriptionResId = CommonR.string.text_discover,
        iconImageVector = Icons.Outlined.RemoveRedEye,
        selectedImageVector = Icons.Default.RemoveRedEye
    ),
    HomeNavigationItem.ImageVectorIcon(
        screen = RootScreen.Search,
        labelResId = CommonR.string.title_search,
        contentDescriptionResId = CommonR.string.title_search,
        iconImageVector = Icons.Outlined.Search,
        selectedImageVector = Icons.Default.Search
    )
)

@Composable
private fun HomeNavigationItemIcon(item: HomeNavigationItem, selected: Boolean) {
    val painter = when (item) {
        is HomeNavigationItem.ResourceIcon -> painterResource(item.iconResId)
        is HomeNavigationItem.ImageVectorIcon -> rememberVectorPainter(item.iconImageVector)
    }
    val selectedPainter = when (item) {
        is HomeNavigationItem.ResourceIcon -> item.selectedIconResId?.let { painterResource(it) }
        is HomeNavigationItem.ImageVectorIcon -> item.selectedImageVector?.let { rememberVectorPainter(it) }
    }

    if (selectedPainter != null) {
        Crossfade(targetState = selected, label = "SelectedIcon") {
            Icon(
                painter = if (it) selectedPainter else painter,
                contentDescription = stringResource(item.contentDescriptionResId)
            )
        }
    } else {
        Icon(
            painter = painter,
            contentDescription = stringResource(item.contentDescriptionResId)
        )
    }
}
