package com.afterroot.ui.common.compose.navigation

import androidx.annotation.StringRes
import com.afterroot.watchdone.resources.R as CommonR
import info.movito.themoviedbapi.model.Multi

sealed class RootScreen(val route: String) {
    object Watchlist : RootScreen("watchlist")
    object Discover : RootScreen("discover")
    object Search : RootScreen("search")
    object Profile : RootScreen("profile")
}

sealed class Screen(val route: String, @StringRes val title: Int) {

    fun createRoute(root: RootScreen) = "${root.route}/$route"

    object Watchlist : Screen("watchlist", CommonR.string.title_watchlist)
    object Discover : Screen("discover", CommonR.string.text_discover)
    object Search : Screen("search", CommonR.string.title_search)

    object MediaInfo : Screen("media/{type}/{mediaId}", CommonR.string.title_home) {
        fun createRoute(root: RootScreen, type: Multi.MediaType, id: Int): String {
            return "${root.route}/media/$type/$id"
        }
    }

    object Profile : Screen("profile", CommonR.string.title_profile)
}
