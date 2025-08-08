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

package com.afterroot.watchdone.data.tmdb.auth

import android.content.SharedPreferences
import androidx.core.content.edit
import app.tivi.util.Logger
import com.afterroot.watchdone.base.Constants
import com.afterroot.watchdone.base.CoroutineDispatchers
import com.afterroot.watchdone.base.InvalidResultException
import com.afterroot.watchdone.di.Tmdb
import com.afterroot.watchdone.utils.launchOrThrow
import dagger.Lazy
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

@Singleton
class TmdbAuthRepository @Inject constructor(
  scope: CoroutineScope,
  private val dispatchers: CoroutineDispatchers,
  private val authStore: AuthStore,
  private val tmdbAuthActions: Lazy<TmdbAuthActions>,
  private val logger: Logger,
  @Tmdb private val prefs: SharedPreferences,
) {
  private val authState = MutableStateFlow<AuthState?>(null)
  private var authStateExpiry: Instant = Instant.DISTANT_PAST

  val state: Flow<TmdbAuthLoginState> = authState.map {
    when (it?.isAuthorized) {
      true -> TmdbAuthLoginState.LOGGED_IN
      else -> TmdbAuthLoginState.LOGGED_OUT
    }
  }

  fun isLoggedIn(): Boolean = authState.value?.isAuthorized == true
  fun getAuthStateBlocking() = runBlocking {
    getAuthState()
  }

  init {
    // Read the auth state from the AuthStore
    scope.launchOrThrow {
      val state = getAuthState() ?: AuthState.Empty
      updateAuthState(authState = state, persist = false)
    }
  }

  suspend fun getAuthState(): AuthState? {
    val state = authState.value
    if (state != null && state.isAuthorized && Clock.System.now() < authStateExpiry) {
      logger.d { "getAuthState. Using cached tokens: $state" }
      return state
    }

    logger.d { "getAuthState. Retrieving tokens from AuthStore" }
    return withContext(dispatchers.io) { authStore.get() }
      ?.also { cacheAuthState(it) }
  }

  suspend fun login(requestToken: String): AuthState? {
    logger.d { "login()" }
    return tmdbAuthActions.get().createSession(requestToken).also {
      logger.d { "Login finished. Result: $it" }
      updateAuthState(authState = it ?: AuthState.Empty)
    }
  }

  suspend fun logout() {
    authState.value?.sessionId?.let {
      updateAuthState(authState = AuthState.Empty)
      tmdbAuthActions.get().deleteSession(it).also {
        logger.d { "logout: delete tmdb session $it" }
      }
    }
  }

  private fun cacheAuthState(authState: AuthState) {
    this.authState.update { authState }
    authStateExpiry = when {
      authState.isAuthorized -> Clock.System.now() + 1.hours
      else -> Instant.DISTANT_PAST
    }
  }

  suspend fun getAuthorizationUrl(): String {
    logger.d { "getAuthorizationUrl()" }
    val token = tmdbAuthActions.get().getToken()

    prefs.edit(commit = true) {
      putString("key_tmdb_request_token", token)
    }

    val url = token?.let {
      tmdbAuthActions.get().buildAuthorizationUrl(
        token = it,
        redirectTo = "${Constants.scheme_watchdone}://${Constants.WATCHDONE_HOST}/tmdb/auth/success",
      )
    }
    if (url == null) {
      throw InvalidResultException("result of getAuthorizationUrl() is null")
    }
    return url
  }

  private suspend fun updateAuthState(authState: AuthState, persist: Boolean = true) {
    if (persist) {
      // Persist auth state
      withContext(dispatchers.io) {
        if (authState.isAuthorized) {
          authStore.save(authState)
          logger.d { "Saved state to AuthStore: $authState" }
        } else {
          authStore.clear()
          logger.d { "Cleared AuthStore" }
        }
      }
    }

    logger.d { "updateAuthState: $authState. Persist: $persist" }
    cacheAuthState(authState)

    /*logger.d { "updateAuthState: Clearing TraktClient auth tokens" }
    traktClient.value.invalidateAuthTokens()*/
  }
}

enum class TmdbAuthLoginState {
  LOGGED_IN,
  LOGGED_OUT,
}
