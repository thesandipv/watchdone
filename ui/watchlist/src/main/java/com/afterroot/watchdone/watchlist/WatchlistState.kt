/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.watchlist

import app.tivi.api.UiMessage
import com.afterroot.watchdone.base.WatchlistType
import com.afterroot.watchdone.base.compose.ViewState
import com.afterroot.watchdone.data.model.Filters

data class WatchlistState(
  val loading: Boolean = false,
  val sortAscending: Boolean = false,
  val filters: Filters = Filters.EMPTY,
  val watchlistType: WatchlistType = WatchlistType.GRID,
  override val message: UiMessage? = null,
) : ViewState() {
  companion object {
    val Empty = WatchlistState()
  }
}
