package com.afterroot.ui.common.compose.navigation

import androidx.annotation.StringRes
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.resources.R as CommonR

sealed class RootScreen(val route: String) {
    data object Watchlist : RootScreen("watchlist")
    data object Discover : RootScreen("discover")
    data object Search : RootScreen("search")
    data object Profile : RootScreen("profile")
}

sealed class Screen(private val route: String, @StringRes val title: Int) {

    fun createRoute(root: RootScreen) = "${root.route}/$route"

    data object Watchlist : Screen("watchlist", CommonR.string.title_watchlist)
    data object Discover : Screen("discover", CommonR.string.text_discover)
    data object Search : Screen("search", CommonR.string.title_search)

    data object MediaInfo : Screen("media/{type}/{mediaId}", CommonR.string.title_home) {
        fun createRoute(root: RootScreen, type: MediaType, id: Int): String {
            return "${root.route}/media/${type.name}/$id"
        }
    }

    data object Profile : Screen("profile", CommonR.string.title_profile)

    data object EditProfile : Screen("editProfile", CommonR.string.title_edit_profile)
}
