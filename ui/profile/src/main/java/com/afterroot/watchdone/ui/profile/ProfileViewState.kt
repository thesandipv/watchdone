/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.ui.profile

import androidx.compose.runtime.Immutable
import app.moviebase.tmdb.model.TmdbAccountDetails
import app.tivi.api.UiMessage
import com.afterroot.watchdone.base.compose.ViewState
import com.afterroot.watchdone.data.model.LocalUser
import com.afterroot.watchdone.data.tmdb.auth.TmdbAuthLoginState
import com.afterroot.watchdone.utils.State

@Immutable
data class ProfileViewState(
  override val message: UiMessage? = null,
  val user: State<LocalUser>? = null,
  val wlCount: State<Long> = State.loading(),
  val isTmdbLoggedIn: TmdbAuthLoginState = TmdbAuthLoginState.LOGGED_OUT,
  val tmdbProfile: State<TmdbAccountDetails>? = null,
) : ViewState() {
  companion object {
    val Empty = ProfileViewState()
  }
}
