/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afterroot.watchdone.data.tmdb.auth.AuthState
import com.afterroot.watchdone.domain.interactors.TmdbLogin
import com.afterroot.watchdone.settings.Settings
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class TmdbLoginViewModel @Inject constructor(
  private val settings: Settings,
  private val tmdbLogin: TmdbLogin,
) : ViewModel() {

  fun loginTmdbAfterSuccessCallback(result: (AuthState?) -> Unit) {
    viewModelScope.launch {
      settings.tmdbRequestToken?.let {
        tmdbLogin(TmdbLogin.TmdbLoginParams(it)).onSuccess { authState ->
          result(authState)
          if (authState != null && authState.isAuthorized) {
            settings.removeTmdbLoginRequestToken()
          }
        }
      }
    }
  }
}
