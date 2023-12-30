/*
 * Copyright (C) 2020-2023 Sandip Vaghela
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
package com.afterroot.watchdone.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import app.tivi.util.Logger
import com.afterroot.utils.extensions.getPrefs
import com.afterroot.watchdone.base.Constants
import com.afterroot.watchdone.base.WatchlistType
import com.afterroot.watchdone.data.model.LocalUser
import com.afterroot.watchdone.data.model.MediaType
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Helper Class for managing main preferences of App
 */
class Settings @Inject constructor(
    @ApplicationContext val context: Context,
    private val gson: Gson,
    private val logger: Logger,
) {

    init {
        logger.d { "Initializing Settings..." }
    }

    private val preferences: SharedPreferences = context.getPrefs()

    fun putString(key: String, value: String?) = preferences.edit(true) {
        putString(key, value)
    }.also {
        logger.d { "putString: $key, $value" }
    }

    fun getString(key: String, value: String?): String? = preferences.getString(key, value).also {
        logger.d { "getString: $key, $it" }
    }

    fun putInt(key: String, value: Int) = preferences.edit(true) {
        putInt(key, value)
    }.also {
        logger.d { "putInt: $key, $value" }
    }

    fun getInt(key: String, value: Int): Int = preferences.getInt(key, value).also {
        logger.d { "getInt: $key, $it" }
    }

    fun putBoolean(key: String, value: Boolean) = preferences.edit(true) {
        putBoolean(key, value)
    }.also {
        logger.d { "putBoolean: $key, $value" }
    }

    fun getBoolean(key: String, value: Boolean): Boolean = preferences.getBoolean(key, value).also {
        logger.d { "getBoolean: $key, $it" }
    }

    fun getStringSet(key: String, value: MutableSet<String>?) = preferences.getStringSet(
        key,
        value,
    ).also {
        logger.d { "getStringSet: $key, $it" }
    }

    private fun putStringSet(key: String, value: MutableSet<String>?) = preferences.edit(true) {
        putStringSet(key, value)
    }.also {
        logger.d { "putStringSet: $key, $value" }
    }

    val defaultImagesSize = "w342"

    // Template
    var isFirstInstalled
        get() = getBoolean(Constants.PREF_KEY_FIRST_INSTALL, true)
        set(value) = putBoolean(Constants.PREF_KEY_FIRST_INSTALL, value)

    var baseUrl
        get() = getString(Constants.PREF_KEY_BASE_IMAGE_URL, null)
        set(value) = putString(Constants.PREF_KEY_BASE_IMAGE_URL, value)

    var posterSizes: MutableSet<String>?
        get() = getStringSet(Constants.PREF_KEY_POSTER_SIZES, null)
        set(value) = putStringSet(Constants.PREF_KEY_POSTER_SIZES, value)

    var imageSize: String?
        get() = getString(Constants.PREF_KEY_IMAGE_SIZE, defaultImagesSize)
        set(value) = putString(Constants.PREF_KEY_IMAGE_SIZE, value)

    var ascSort: Boolean
        get() = getBoolean(Constants.PREF_KEY_SORT_ORDER, false)
        set(value) = putBoolean(Constants.PREF_KEY_SORT_ORDER, value)

    var queryDirection: Query.Direction
        get() = if (ascSort) Query.Direction.ASCENDING else Query.Direction.DESCENDING
        set(value) {
            ascSort = value == Query.Direction.ASCENDING
        }

    @DebugPref
    val isUseProdDb: Boolean
        get() = getBoolean("use_prod_db", false)

    @DebugPref
    val isHttpLogging: Boolean
        get() = getBoolean("http_logging", false)

    var isUsernameSet: Boolean
        get() = getBoolean("is_user_name_set", false)
        set(value) = putBoolean("is_user_name_set", value)

    var userProfile: LocalUser
        get() = gson.fromJson(getString("profile", gson.toJson(LocalUser())), LocalUser::class.java)
        set(value) = putString("profile", gson.toJson(value))

    var country: String?
        get() = getString("key_country", null)
        set(value) = putString("key_country", value)

    var watchlistType: WatchlistType
        get() = WatchlistType.valueOf(
            getString("watchlist_type", WatchlistType.GRID.name) ?: WatchlistType.GRID.name,
        )
        set(value) = putString("watchlist_type", value.name)

    var discoverMediaType: MediaType
        get() = MediaType.valueOf(
            getString("discover_media_type", MediaType.MOVIE.name) ?: MediaType.MOVIE.name,
        )
        set(value) = putString("discover_media_type", value.name)

    // Helper Functions
    fun createPosterUrl(path: String) = baseUrl + imageSize + path

    fun signOut() {
        isUsernameSet = false
        putString("profile", null)
    }
}
