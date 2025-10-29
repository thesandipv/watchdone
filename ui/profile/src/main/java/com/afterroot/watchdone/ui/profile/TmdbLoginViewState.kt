/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.ui.profile

import androidx.compose.runtime.Immutable
import app.tivi.api.UiMessage
import com.afterroot.watchdone.base.compose.ViewState
import com.afterroot.watchdone.data.tmdb.auth.TmdbAuthLoginState

@Immutable
data class TmdbLoginViewState(
  override val message: UiMessage? = null,
  val isTmdbLoggedIn: TmdbAuthLoginState = TmdbAuthLoginState.LOGGED_OUT,
) : ViewState() {
  companion object {
    val Empty = TmdbLoginViewState()
  }
}
