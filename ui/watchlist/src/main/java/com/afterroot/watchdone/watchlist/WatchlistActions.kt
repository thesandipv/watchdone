package com.afterroot.watchdone.watchlist

import com.afterroot.watchdone.base.compose.Actions
import com.afterroot.watchdone.data.QueryAction

sealed class WatchlistActions : Actions() {
    data class SetQueryAction(val queryAction: QueryAction) : WatchlistActions()
    object Refresh : WatchlistActions()
}
