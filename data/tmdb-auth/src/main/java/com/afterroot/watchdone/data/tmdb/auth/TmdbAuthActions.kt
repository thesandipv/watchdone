/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.tmdb.auth

interface TmdbAuthActions {
  suspend fun getToken(): String?
  suspend fun buildAuthorizationUrl(token: String, redirectTo: String): String
  suspend fun createSession(requestToken: String): AuthState?
  suspend fun deleteSession(sessionId: String): Boolean
}
