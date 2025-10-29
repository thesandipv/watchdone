/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.tmdb.auth

interface AuthStore {
  suspend fun get(): AuthState?
  suspend fun save(authState: AuthState)
  suspend fun clear()
  suspend fun isAvailable(): Boolean = true
}

interface AuthState {
  val sessionId: String
  val isAuthorized: Boolean
  fun serializeToJson(): String

  companion object {
    val Empty: AuthState get() = EmptyAuthState
  }
}

private data object EmptyAuthState : AuthState {
  override val sessionId: String = ""
  override val isAuthorized: Boolean = false
  override fun serializeToJson(): String = "{}"
}
