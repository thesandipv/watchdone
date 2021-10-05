/*
 * Copyright (C) 2020-2021 Sandip Vaghela
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
import androidx.preference.PreferenceManager
import com.afterroot.watchdone.base.Constants
import com.afterroot.watchdone.data.R
import com.afterroot.watchdone.data.model.LocalUser
import com.google.firebase.firestore.Query
import com.google.gson.Gson

/**
 * Helper Class for managing main preferences of App
 */
class Settings(
    private val context: Context,
    private val gson: Gson
) {

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private fun putString(key: String, value: String?) = preferences.edit(true) {
        putString(key, value)
    }

    private fun putInt(key: String, value: Int) = preferences.edit(true) {
        putInt(key, value)
    }

    private fun putBoolean(key: String, value: Boolean) = preferences.edit(true) {
        putBoolean(key, value)
    }

    private fun putStringSet(key: String, value: MutableSet<String>?) = preferences.edit(true) {
        putStringSet(key, value)
    }

    // Template
    var isFirstInstalled
        get() = preferences.getBoolean(Constants.PREF_KEY_FIRST_INSTALL, true)
        set(value) = putBoolean(Constants.PREF_KEY_FIRST_INSTALL, value)
    var baseUrl
        get() = preferences.getString(Constants.PREF_KEY_BASE_IMAGE_URL, null)
        set(value) = putString(Constants.PREF_KEY_BASE_IMAGE_URL, value)
    var posterSizes: MutableSet<String>?
        get() = preferences.getStringSet(Constants.PREF_KEY_POSTER_SIZES, null)
        set(value) = putStringSet(Constants.PREF_KEY_POSTER_SIZES, value)
    var imageSize: String?
        get() = preferences.getString(Constants.PREF_KEY_IMAGE_SIZE, "w342")
        set(value) = putString(Constants.PREF_KEY_IMAGE_SIZE, value)
    var ascSort: Boolean
        get() = preferences.getBoolean(Constants.PREF_KEY_SORT_ORDER, false)
        set(value) = putBoolean(Constants.PREF_KEY_SORT_ORDER, value)
    var queryDirection: Query.Direction
        get() = if (ascSort) Query.Direction.ASCENDING else Query.Direction.DESCENDING
        set(value) {
            ascSort = value == Query.Direction.ASCENDING
        }
    val theme: String?
        get() = preferences.getString(Constants.PREF_KEY_THEME, context.getString(R.string.theme_device_default))
    val isUseProdDb: Boolean
        get() = preferences.getBoolean("use_prod_db", false)

    var isUsernameSet: Boolean
        get() = preferences.getBoolean("is_user_name_set", false)
        set(value) = putBoolean("is_user_name_set", value)

    var userProfile: LocalUser
        get() = gson.fromJson(preferences.getString("profile", gson.toJson(LocalUser())), LocalUser::class.java)
        set(value) = putString("profile", gson.toJson(value))

    // Helper Functions
    fun createPosterUrl(path: String) = baseUrl + imageSize + path

    fun signOut() {
        isUsernameSet = false
        putString("profile", null)
    }
}
