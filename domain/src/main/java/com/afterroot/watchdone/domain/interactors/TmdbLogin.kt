/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.domain.interactors

import app.tivi.domain.Interactor
import com.afterroot.watchdone.data.tmdb.auth.AuthState
import com.afterroot.watchdone.data.tmdb.auth.TmdbAuthRepository
import dagger.Lazy
import javax.inject.Inject

class TmdbLogin @Inject constructor(private val tmdbAuthRepository: Lazy<TmdbAuthRepository>) :
  Interactor<TmdbLogin.TmdbLoginParams, AuthState?>() {
  override suspend fun doWork(params: TmdbLoginParams): AuthState? =
    tmdbAuthRepository.get().login(params.requestToken)

  data class TmdbLoginParams(val requestToken: String)
}

class TmdbLogout @Inject constructor(private val tmdbAuthRepository: Lazy<TmdbAuthRepository>) :
  Interactor<Unit, Unit>() {
  override suspend fun doWork(params: Unit) = tmdbAuthRepository.get().logout()
}

class TmdbGetAuthorizationUrl @Inject constructor(
  private val tmdbAuthRepository: Lazy<TmdbAuthRepository>,
) : Interactor<Unit, String>() {
  override suspend fun doWork(params: Unit) = tmdbAuthRepository.get().getAuthorizationUrl()
}
