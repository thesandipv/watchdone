/*
 * Copyright (C) 2020-2022 Sandip Vaghela
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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.PlaylistAddCheck
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.afterroot.watchdone.ui.common.Screen
import com.afterroot.watchdone.watchlist.Watchlist

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home() {
    val navController = rememberNavController()
    val currentSelectedItem by navController.currentScreenAsState()

    Scaffold(
        bottomBar = {
            HomeBottomNavigation(
                selectedNavigation = currentSelectedItem,
                onNavigationSelected = { selected ->
                    navController.navigate(selected.route) {
                        launchSingleTop = true
                        popUpTo(Screen.Watchlist.route) {
                            inclusive = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) {
        Box(Modifier.fillMaxWidth()) {
            NavHost(navController = navController, startDestination = Screen.Watchlist.route) {
                composable(Screen.Watchlist.route) {
                    Watchlist(navController = navController)
                }

                composable(Screen.Discover.route) {
                    Text(text = Screen.Discover.route)
                }

                composable(Screen.Settings.route) {
                    Text(text = Screen.Settings.route)
                }
            }
        }
    }
}

/**
 * Adds an [NavController.OnDestinationChangedListener] to this [NavController] and updates the return [State]
 * as the destination changes.
 */
@Composable
private fun NavController.currentScreenAsState(): State<Screen> {
    val selectedItem = remember { mutableStateOf(Screen.Watchlist) }

    DisposableEffect(this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            when {
                destination.matchesRoute(Screen.Watchlist.route) -> {
                    selectedItem.value = Screen.Watchlist
                }
                destination.matchesRoute(Screen.Discover.route) -> {
                    selectedItem.value = Screen.Discover
                }
                destination.matchesRoute(Screen.Settings.route) -> {
                    selectedItem.value = Screen.Settings
                }
                // We intentionally ignore any other destinations, as they're likely to be
                // leaf destinations.
            }
        }
        addOnDestinationChangedListener(listener)

        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }

    return selectedItem
}

/**
 * Returns true if this [NavDestination] matches the given route.
 */
private fun NavDestination.matchesRoute(route: String): Boolean {
    // Copied from Compose-Navigation NavGraphBuilder.kt
    return hasDeepLink("android-app://androidx.navigation.compose/$route".toUri())
}

@Composable
internal fun HomeBottomNavigation(
    selectedNavigation: Screen,
    onNavigationSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {

    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.PlaylistAddCheck,
                    contentDescription = "Watchlist"
                )
            },
            label = { Text("Watchlist") },
            selected = selectedNavigation == Screen.Watchlist,
            onClick = { onNavigationSelected(Screen.Watchlist) },
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Dashboard,
                    contentDescription = "Discover"
                )
            },
            label = { Text("Discover") },
            selected = selectedNavigation == Screen.Discover,
            onClick = { onNavigationSelected(Screen.Discover) },
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings"
                )
            },
            label = { Text("Settings") },
            selected = selectedNavigation == Screen.Settings,
            onClick = { onNavigationSelected(Screen.Settings) },
        )
    }
}
