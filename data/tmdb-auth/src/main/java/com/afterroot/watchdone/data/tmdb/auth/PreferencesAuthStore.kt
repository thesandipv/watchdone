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
import com.afterroot.watchdone.di.Tmdb
import javax.inject.Inject

class PreferencesAuthStore @Inject constructor(@Tmdb private val prefs: SharedPreferences) :
  AuthStore {
  override suspend fun get(): AuthState? = prefs.getString(KEY, null)?.let(::TmdbAuthStateWrapper)

  override suspend fun save(authState: AuthState) {
    prefs.edit(commit = true) {
      putString(KEY, authState.serializeToJson())
    }
  }

  override suspend fun clear() {
    prefs.edit(commit = true) {
      remove(KEY)
    }
  }

  private companion object {
    private const val KEY = "stateJson"
  }
}
