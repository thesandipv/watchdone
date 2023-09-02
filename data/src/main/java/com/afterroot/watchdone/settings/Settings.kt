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
import com.afterroot.utils.extensions.getPrefs
import com.afterroot.watchdone.base.Constants
import com.afterroot.watchdone.data.model.LocalUser
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import timber.log.Timber
import com.afterroot.watchdone.resources.R as CommonR

/**
 * Helper Class for managing main preferences of App
 */
class Settings @Inject constructor(
    @ApplicationContext val context: Context,
    val gson: Gson,
) {

    init {
        Timber.d("Initializing Settings...")
    }

    private val preferences: SharedPreferences = context.getPrefs()

    fun putString(key: String, value: String?) = preferences.edit(true) {
        putString(key, value)
    }.also {
        Timber.d("putString: $key, $value")
    }

    fun getString(key: String, value: String?): String? = preferences.getString(key, value).also {
        Timber.d("getString: $key, $it")
    }

    fun putInt(key: String, value: Int) = preferences.edit(true) {
        putInt(key, value)
    }.also {
        Timber.d("putInt: $key, $value")
    }

    fun getInt(key: String, value: Int): Int = preferences.getInt(key, value).also {
        Timber.d("getInt: $key, $it")
    }

    fun putBoolean(key: String, value: Boolean) = preferences.edit(true) {
        putBoolean(key, value)
    }.also {
        Timber.d("putBoolean: $key, $value")
    }

    fun getBoolean(key: String, value: Boolean): Boolean = preferences.getBoolean(key, value).also {
        Timber.d("getBoolean: $key, $it")
    }

    fun getStringSet(key: String, value: MutableSet<String>?) = preferences.getStringSet(
        key,
        value,
    ).also {
        Timber.d("getStringSet: $key, $it")
    }

    private fun putStringSet(key: String, value: MutableSet<String>?) = preferences.edit(true) {
        putStringSet(key, value)
    }.also {
        Timber.d("putStringSet: $key, $value")
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

    val theme: String?
        get() = getString(
            Constants.PREF_KEY_THEME,
            context.getString(CommonR.string.theme_device_default),
        )

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

    // Helper Functions
    fun createPosterUrl(path: String) = baseUrl + imageSize + path

    fun signOut() {
        isUsernameSet = false
        putString("profile", null)
    }
}
