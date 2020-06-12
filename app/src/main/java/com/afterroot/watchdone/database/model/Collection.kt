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

package com.afterroot.watchdone.database.model

object Collection {
    //Collections
    const val USERS = "users"
    const val WATCHDONE = "watchdone"
    const val WATCHLIST = "watchlist"
    const val ITEMS = "items"
}

object Field {
    //Fields
    const val NAME = "name"
    const val EMAIL = "email"
    const val UID = "uid"
    const val FCM_ID = "fcmId"
    const val TOTAL_ITEMS = "total_items"
    const val RELEASE_DATE = "releaseDate"
    const val IS_WATCHED = "isWatched"
    const val ID = "id"
}