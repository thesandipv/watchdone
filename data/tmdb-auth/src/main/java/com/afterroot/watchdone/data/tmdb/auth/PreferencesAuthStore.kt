/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
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
