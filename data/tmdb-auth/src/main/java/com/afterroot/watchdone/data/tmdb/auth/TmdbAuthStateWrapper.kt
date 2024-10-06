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
