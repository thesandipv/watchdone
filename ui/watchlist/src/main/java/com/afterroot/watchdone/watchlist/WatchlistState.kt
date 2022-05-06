package com.afterroot.watchdone.watchlist

import com.afterroot.watchdone.base.compose.ViewState

data class WatchlistState(val loading: Boolean = false) : ViewState() {
    companion object {
        val INITIAL = WatchlistState()
    }
}
