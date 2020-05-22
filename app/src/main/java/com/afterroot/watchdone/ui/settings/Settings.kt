/*
 * Copyright (C) 2020 Sandip Vaghela
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

package com.afterroot.watchdone.ui.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.afterroot.watchdone.Constants

class Settings(context: Context) {

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val mContext = context
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

    //Template
    var isFirstInstalled
        get() = preferences.getBoolean(Constants.PREF_KEY_FIRST_INSTALL, true)
        set(value) = putBoolean(Constants.PREF_KEY_FIRST_INSTALL, value)
    var baseUrl
        get() = preferences.getString(Constants.PREF_KEY_BASE_IMAGE_URL, null)
        set(value) = putString(Constants.PREF_KEY_BASE_IMAGE_URL, value)
    var posterSizes
        get() = preferences.getStringSet(Constants.PREF_KEY_POSTER_SIZES, null)
        set(value) = putStringSet(Constants.PREF_KEY_POSTER_SIZES, value)
}