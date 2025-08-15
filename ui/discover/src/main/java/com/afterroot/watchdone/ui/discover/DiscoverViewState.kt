/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.ui.discover

import androidx.compose.runtime.Immutable
import app.tivi.api.UiMessage
import com.afterroot.watchdone.base.compose.ViewState
import com.afterroot.watchdone.data.model.MediaType

@Immutable
data class DiscoverViewState(
  val mediaType: MediaType? = MediaType.MOVIE,
  val isLoading: Boolean = false,
  override val message: UiMessage? = null,
) : ViewState() {
  companion object {
    val Empty = DiscoverViewState()
  }
}
