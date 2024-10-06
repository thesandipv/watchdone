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
