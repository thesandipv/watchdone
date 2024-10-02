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

package com.afterroot.watchdone.ui.profile

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import app.tivi.util.Logger
import com.afterroot.ui.common.compose.components.LocalLogger
import com.afterroot.watchdone.viewmodel.ProfileViewModel

@Composable
fun TmdbProfile() {
  TmdbProfile(viewModel = hiltViewModel())
}

@Composable
internal fun TmdbProfile(viewModel: ProfileViewModel, logger: Logger = LocalLogger.current) {
  val viewState by viewModel.state.collectAsState()

  viewModel.loginTmdbAfterSuccessCallback()

  Text("Tmdb Login: ${viewState.isTmdbLoggedIn}")
}
