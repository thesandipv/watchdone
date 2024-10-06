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

import android.view.View
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
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
import com.afterroot.watchdone.ui.profile.TmdbLogin
import com.afterroot.watchdone.ui.search.Search
import com.afterroot.watchdone.watchlist.Watchlist

typealias NavEnterTransition = AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition
typealias NavExitTransition = AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition

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
  appState: AppState,
  modifier: Modifier = Modifier,
  onWatchProviderClick: (link: String) -> Unit = { _ -> },
  settingsAction: () -> Unit,
  shareToIG: ((mediaId: Int, poster: String) -> Unit)? = null,
) {
  NavHost(
    navController = appState.navController,
    startDestination = RootScreen.Watchlist.route,
    modifier = modifier,
    enterTransition = slideInAndFade(
      towards = AnimatedContentTransitionScope.SlideDirection.Up,
      animationSpec = tween(500),
      initialOffset = { 60 },
    ),
    exitTransition = { fadeOut(tween(500)) },
  ) {
    addWatchlistRoot(appState, onWatchProviderClick, settingsAction, shareToIG)
    addDiscoverRoot(appState)
    addSearchRoot(appState)
    addProfileRoot(appState)
  }
}

private fun NavGraphBuilder.addWatchlistRoot(
  appState: AppState,
  onWatchProviderClick: (link: String) -> Unit = { _ -> },
  settingsAction: () -> Unit,
  shareToIG: ((mediaId: Int, poster: String) -> Unit)? = null,
) {
  navigation(
    route = RootScreen.Watchlist.route,
    startDestination = Screen.Watchlist.createRoute(RootScreen.Watchlist),
  ) {
    addWatchlist(appState, RootScreen.Watchlist, settingsAction)
    addMediaInfo(
      appState,
      RootScreen.Watchlist,
      onWatchProviderClick = onWatchProviderClick,
      shareToIG = shareToIG,
    )
  }
}

private fun NavGraphBuilder.addWatchlist(
  appState: AppState,
  rootScreen: RootScreen,
  settingsAction: () -> Unit,
) {
  composable(
    route = Screen.Watchlist.createRoute(rootScreen),
    enterTransition = topDestinationEnterTransition(),
    exitTransition = topDestinationExitTransition(),
  ) {
    Watchlist(
      viewModel = hiltViewModel(),
      settingsAction = settingsAction,
      itemSelectedCallback = itemSelectedCallback(appState.navController),
    )
  }
}

private fun NavGraphBuilder.addMediaInfo(
  appState: AppState,
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
        uriPattern =
          "${Constants.SCHEME_HTTPS}://${Constants.WATCHDONE_HOST}/media/{type}/{mediaId}"
      },
    ),
  ) {
    MediaInfo(
      navigateUp = {
        appState.navController.navigateUp()
      },
      onRecommendedClick = {
        if (it.tmdbId != null) {
          it.mediaType?.let { mediaType ->
            appState.navController.navigate(
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

private fun NavGraphBuilder.addDiscoverRoot(appState: AppState) {
  navigation(
    route = RootScreen.Discover.route,
    startDestination = Screen.Discover.createRoute(RootScreen.Discover),
  ) {
    addDiscover(appState, RootScreen.Discover)
  }
}

private fun NavGraphBuilder.addDiscover(appState: AppState, rootScreen: RootScreen) {
  composable(
    route = Screen.Discover.createRoute(rootScreen),
    enterTransition = topDestinationEnterTransition(),
    exitTransition = topDestinationExitTransition(),
  ) {
    Discover(
      discoverViewModel = hiltViewModel(),
      itemSelectedCallback = itemSelectedCallback(appState.navController),
    )
  }
}

private fun NavGraphBuilder.addSearchRoot(appState: AppState) {
  navigation(
    route = RootScreen.Search.route,
    startDestination = Screen.Search.createRoute(RootScreen.Search),
  ) {
    addSearch(appState, RootScreen.Search)
  }
}

private fun NavGraphBuilder.addSearch(appState: AppState, rootScreen: RootScreen) {
  composable(
    route = Screen.Search.createRoute(rootScreen),
    enterTransition = topDestinationEnterTransition(),
    exitTransition = topDestinationExitTransition(),
    deepLinks = listOf(
      navDeepLink {
        uriPattern = "${Constants.SCHEME_HTTPS}://${Constants.WATCHDONE_HOST}/search"
      },
    ),
  ) {
    Search(
      viewModel = hiltViewModel(),
      itemSelectedCallback = itemSelectedCallback(appState.navController),
    )
  }
}

private fun NavGraphBuilder.addProfileRoot(appState: AppState) {
  navigation(
    route = RootScreen.Profile.route,
    startDestination = Screen.Profile.createRoute(RootScreen.Profile),
  ) {
    addProfile(appState, RootScreen.Profile)
    addTmdbProfile(appState, RootScreen.Profile)
    addEditProfile(appState, RootScreen.Profile)
  }
}

private fun NavGraphBuilder.addProfile(appState: AppState, rootScreen: RootScreen) {
  composable(
    route = Screen.Profile.createRoute(rootScreen),
    enterTransition = topDestinationEnterTransition(),
    exitTransition = topDestinationExitTransition(),
  ) {
    Profile(
      onEditProfile = {
        appState.navController.navigate(Screen.EditProfile.createRoute(rootScreen))
      },
      onWatchlistItemClick = { mediaType, id ->
        appState.navController.navigate(Deeplink.media(id, mediaType))
      },
    )
  }
}

private fun NavGraphBuilder.addTmdbProfile(appState: AppState, rootScreen: RootScreen) {
  composable(
    route = Screen.TmdbProfile.createRoute(rootScreen),
    enterTransition = topDestinationEnterTransition(),
    exitTransition = topDestinationExitTransition(),
    deepLinks = listOf(
      navDeepLink {
        uriPattern = Deeplink.authSuccess
      },
    ),
  ) {
    TmdbLogin { _ ->
      appState.navController.navigate(
        Screen.Profile.createRoute(rootScreen),
        navOptions = NavOptions.Builder()
          .setPopUpTo(
            route = Screen.Profile.createRoute(rootScreen),
            inclusive = true,
          ).build(),
      )
    }
  }
}

private fun NavGraphBuilder.addEditProfile(appState: AppState, rootScreen: RootScreen) {
  composable(
    route = Screen.EditProfile.createRoute(rootScreen),
  ) {
    EditProfile(
      onUpAction = {
        appState.navController.navigateUp()
      },
    )
  }
}

private fun topDestinationExitTransition(): NavExitTransition = {
  fadeOut(tween(500))
}

private fun topDestinationEnterTransition(): NavEnterTransition = slideInAndFade(
  towards = AnimatedContentTransitionScope.SlideDirection.Up,
  animationSpec = tween(500),
  initialOffset = { 60 },
)

private fun slideInAndFade(
  towards: AnimatedContentTransitionScope.SlideDirection,
  animationSpec: FiniteAnimationSpec<IntOffset> =
    spring(visibilityThreshold = IntOffset.VisibilityThreshold),
  initialOffset: (offsetForFullSlide: Int) -> Int = { it },
): NavEnterTransition = {
  slideIntoContainer(towards, animationSpec, initialOffset) + fadeIn(tween(500))
}
