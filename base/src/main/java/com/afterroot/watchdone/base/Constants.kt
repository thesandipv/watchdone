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
package com.afterroot.watchdone.base

object Collection {
    // Collections
    const val USERS = "users"
    const val WATCHDONE_PROD = "watchdone"
    const val WATCHDONE_DEBUG = "watchdone-debug"
    val WATCHDONE_AUTO = if (BuildConfig.DEBUG) WATCHDONE_DEBUG else WATCHDONE_PROD
    const val WATCHLIST = "watchlist"
    const val ITEMS = "items"
}

object Field {
    // Fields
    const val NAME = "name"
    const val EMAIL = "email"
    const val UID = "uid"
    const val FCM_ID = "fcmId"
    const val TOTAL_ITEMS = "total_items"
    const val RELEASE_DATE = "releaseDate"
    const val IS_WATCHED = "isWatched"
    const val ID = "id"
    const val MEDIA_TYPE = "mediaType"
    const val MEDIA_TYPE_MOVIE = "MOVIE"
    const val MEDIA_TYPE_TV = "TV_SERIES"
    const val TIMESTAMP = "timestamp"

    @Deprecated("Deprecated in favor of WATCHED_EPISODES")
    const val WATCH_STATUS = "watchStatus"
    const val WATCHED_EPISODES = "watched"
    const val VERSION = "version"
}

object Constants {
    const val PREF_KEY_BASE_IMAGE_URL = "base_url"
    const val PREF_KEY_FIRST_INSTALL = "first_install_2"
    const val PREF_KEY_IMAGE_SIZE = "image_size"
    const val PREF_KEY_POSTER_SIZES = "poster_sizes"
    const val PREF_KEY_SORT_ORDER = "asc_sort"
    const val PREF_KEY_THEME = "app_theme"
    const val RC_LOGIN = 42
    const val RC_PERMISSION = 256
    const val RC_STORAGE_ACCESS = 12

    const val SCHEME_HTTPS = "https"
    const val WATCHDONE_HOST = "watchdone.web.app"
    const val AFTERROOT_HOST = "afterroot.web.app"

    const val IG_SHARE_IMAGE_SIZE = "w780"
    const val IG_SHARE_ACTION = "com.instagram.share.ADD_TO_STORY"
    const val IG_PACKAGE_NAME = "com.instagram.android"
    const val MIME_TYPE_JPEG = "image/jpeg"
    const val IG_EXTRA_INT_ASSET_URI = "interactive_asset_uri"
    const val IG_EXTRA_CONTENT_URL = "content_url"
    const val IG_EXTRA_TOP_COLOR = "top_background_color"
    const val IG_EXTRA_BOTTOM_COLOR = "bottom_background_color"
    const val IG_EXTRA_SOURCE_APP = "source_application"

    @Suppress("KotlinConstantConditions")
    val IG_SHARE_PROVIDER: String = if (BuildConfig.BUILD_TYPE != "debug") {
        "$BASE_APP_ID.provider"
    } else {
        "$BASE_APP_ID.debug.provider"
    }

    const val USERNAME_LENGTH = 15
    const val NAME_LENGTH = 30
}

const val BASE_APP_ID = "com.afterroot.watchdone"
