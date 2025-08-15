/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.ui.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.afterroot.watchdone.data.tmdb.auth.AuthState
import com.afterroot.watchdone.viewmodel.TmdbLoginViewModel

@Composable
fun TmdbLogin(onLoginResult: (AuthState) -> Unit) {
  TmdbLogin(viewModel = hiltViewModel(), onLoginResult = onLoginResult)
}

@Composable
internal fun TmdbLogin(viewModel: TmdbLoginViewModel, onLoginResult: (AuthState) -> Unit) {
  viewModel.loginTmdbAfterSuccessCallback {
    it?.let {
      onLoginResult(it)
    }
  }

  TmdbLoginContent(modifier = Modifier.fillMaxSize())
}

@Composable
internal fun TmdbLoginContent(modifier: Modifier = Modifier) {
  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    CircularProgressIndicator()
  }
}

@Preview
@Composable
fun PreviewTmdbLogin() {
  TmdbLoginContent(modifier = Modifier.fillMaxSize())
}
