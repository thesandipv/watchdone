/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.ui.search

import androidx.compose.runtime.Immutable
import app.tivi.api.UiMessage
import com.afterroot.watchdone.base.compose.ViewState
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.data.model.Query

@Immutable
data class SearchViewState(
  val mediaType: MediaType? = MediaType.MOVIE,
  val query: Query = Query(),
  val isLoading: Boolean = false,
  val refresh: Boolean = false,
  val empty: Boolean = false,
  override val message: UiMessage? = null,
) : ViewState() {
  companion object {
    val Empty = SearchViewState()
  }
}
