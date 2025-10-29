/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.tmdb.auth

import app.moviebase.tmdb.model.TmdbSession
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TmdbAuthStateWrapper(private val session: TmdbSession) : AuthState {
  constructor(json: String) : this(Json.decodeFromString<TmdbSession>(json))

  override val sessionId: String
    get() = session.sessionId
  override val isAuthorized: Boolean
    get() = session.success

  override fun serializeToJson(): String = Json.encodeToString(TmdbSession(isAuthorized, sessionId))
}
