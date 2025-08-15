/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.tmdb.auth

import app.moviebase.tmdb.Tmdb3
import app.moviebase.tmdb.url.TmdbAuthenticationUrlBuilder
import javax.inject.Inject

class TmdbAuthActionsImpl @Inject constructor(val tmdb3: Tmdb3) : TmdbAuthActions {
  override suspend fun getToken(): String? {
    val token = tmdb3.authentication.requestToken()
    return if (token.success) {
      token.requestToken
    } else {
      null
    }
  }

  override suspend fun buildAuthorizationUrl(token: String, redirectTo: String): String =
    TmdbAuthenticationUrlBuilder.buildAuthorizationUrl(token, redirectTo)

  override suspend fun createSession(requestToken: String): AuthState =
    TmdbAuthStateWrapper(tmdb3.authentication.createSession(requestToken))

  override suspend fun deleteSession(sessionId: String): Boolean =
    tmdb3.authentication.deleteSession(sessionId).success
}
