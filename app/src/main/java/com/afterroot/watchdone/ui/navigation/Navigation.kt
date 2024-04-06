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

package com.afterroot.watchdone.ui.navigation

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.afterroot.ui.common.compose.navigation.RootScreen
import com.afterroot.ui.common.compose.navigation.Screen
import com.afterroot.watchdone.base.Constants
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.helpers.Deeplink
import com.afterroot.watchdone.ui.common.ItemSelectedCallback
import com.afterroot.watchdone.ui.discover.Discover
import com.afterroot.watchdone.ui.media.MediaInfo
import com.afterroot.watchdone.ui.profile.EditProfile
import com.afterroot.watchdone.ui.profile.Profile
import com.afterroot.watchdone.ui.search.Search
import com.afterroot.watchdone.watchlist.Watchlist

fun itemSelectedCallback(navController: NavHostController) = object : ItemSelectedCallback<Media> {
  override fun onClick(position: Int, view: View?, item: Media) {
    if (item.mediaType == MediaType.MOVIE || item.mediaType == MediaType.SHOW) {
      item.tmdbId?.let {
        val request = NavDeepLinkRequest.Builder
          .fromUri(Deeplink.media(it, item.mediaType!!))
          .build()
        navController.navigate(request)
      }
    }
  }
}

@Composable
fun AppNavigation(
  navController: NavHostController,
  modifier: Modifier = Modifier,
  onWatchProviderClick: (link: String) -> Unit = { _ -> },
  settingsAction: () -> Unit,
  shareToIG: ((mediaId: Int, poster: String) -> Unit)? = null,
) {
  NavHost(
    navController = navController,
    startDestination = RootScreen.Watchlist.route,
    modifier = modifier,
  ) {
    addWatchlistRoot(navController, onWatchProviderClick, settingsAction, shareToIG)
    addDiscoverRoot(navController)
    addSearchRoot(navController)
    addProfileRoot(navController)
  }
}

private fun NavGraphBuilder.addWatchlistRoot(
  navController: NavHostController,
  onWatchProviderClick: (link: String) -> Unit = { _ -> },
  settingsAction: () -> Unit,
  shareToIG: ((mediaId: Int, poster: String) -> Unit)? = null,
) {
  navigation(
    route = RootScreen.Watchlist.route,
    startDestination = Screen.Watchlist.createRoute(RootScreen.Watchlist),
  ) {
    addWatchlist(navController, RootScreen.Watchlist, settingsAction)
    addMediaInfo(
      navController,
      RootScreen.Watchlist,
      onWatchProviderClick = onWatchProviderClick,
      shareToIG = shareToIG,
    )
  }
}

private fun NavGraphBuilder.addWatchlist(
  navController: NavHostController,
  rootScreen: RootScreen,
  settingsAction: () -> Unit,
) {
  composable(route = Screen.Watchlist.createRoute(rootScreen)) {
    Watchlist(
      viewModel = hiltViewModel(),
      settingsAction = settingsAction,
      itemSelectedCallback = itemSelectedCallback(navController),
    )
  }
}

private fun NavGraphBuilder.addMediaInfo(
  navController: NavHostController,
  rootScreen: RootScreen,
  onWatchProviderClick: (link: String) -> Unit = { _ -> },
  shareToIG: ((mediaId: Int, poster: String) -> Unit)? = null,
) {
  composable(
    route = Screen.MediaInfo.createRoute(rootScreen),
    arguments = listOf(
      navArgument("type") {
        type = NavType.StringType
      },
      navArgument("mediaId") {
        type = NavType.IntType
      },
    ),
    deepLinks = listOf(
      navDeepLink {
        uriPattern = "${Constants.SCHEME_HTTPS}://${Constants.WATCHDONE_HOST}/media/{type}/{mediaId}"
      },
    ),
  ) {
    MediaInfo(
      navigateUp = {
        navController.navigateUp()
      },
      onRecommendedClick = {
        if (it.tmdbId != null) {
          it.mediaType?.let { mediaType ->
            navController.navigate(
              Screen.MediaInfo.createRoute(rootScreen, mediaType, it.tmdbId!!),
            )
          }
        }
      },
      onWatchProviderClick = onWatchProviderClick,
      shareToIG = shareToIG,
    )
  }
}

private fun NavGraphBuilder.addDiscoverRoot(navController: NavHostController) {
  navigation(
    route = RootScreen.Discover.route,
    startDestination = Screen.Discover.createRoute(RootScreen.Discover),
  ) {
    addDiscover(navController, RootScreen.Discover)
  }
}

private fun NavGraphBuilder.addDiscover(navController: NavHostController, rootScreen: RootScreen) {
  composable(route = Screen.Discover.createRoute(rootScreen)) {
    Discover(
      discoverViewModel = hiltViewModel(),
      itemSelectedCallback = itemSelectedCallback(navController),
    )
  }
}

private fun NavGraphBuilder.addSearchRoot(navController: NavHostController) {
  navigation(
    route = RootScreen.Search.route,
    startDestination = Screen.Search.createRoute(RootScreen.Search),
  ) {
    addSearch(navController, RootScreen.Search)
  }
}

private fun NavGraphBuilder.addSearch(navController: NavHostController, rootScreen: RootScreen) {
  composable(
    route = Screen.Search.createRoute(rootScreen),
    deepLinks = listOf(
      navDeepLink {
        uriPattern = "${Constants.SCHEME_HTTPS}://${Constants.WATCHDONE_HOST}/search"
      },
    ),
  ) {
    Search(
      viewModel = hiltViewModel(),
      itemSelectedCallback = itemSelectedCallback(navController),
    )
  }
}

private fun NavGraphBuilder.addProfileRoot(navController: NavHostController) {
  navigation(
    route = RootScreen.Profile.route,
    startDestination = Screen.Profile.createRoute(RootScreen.Profile),
  ) {
    addProfile(navController, RootScreen.Profile)
    addEditProfile(navController, RootScreen.Profile)
  }
}

private fun NavGraphBuilder.addProfile(navController: NavHostController, rootScreen: RootScreen) {
  composable(route = Screen.Profile.createRoute(rootScreen)) {
    Profile {
      navController.navigate(Screen.EditProfile.createRoute(rootScreen))
    }
  }
}

private fun NavGraphBuilder.addEditProfile(navController: NavHostController, rootScreen: RootScreen) {
  composable(route = Screen.EditProfile.createRoute(rootScreen)) {
    EditProfile()
  }
}
