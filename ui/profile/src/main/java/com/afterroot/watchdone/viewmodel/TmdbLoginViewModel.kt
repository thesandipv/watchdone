/*
 * Copyright (C) 2020-2024 Sandip Vaghela
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
