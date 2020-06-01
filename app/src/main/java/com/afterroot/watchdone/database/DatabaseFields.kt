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

package com.afterroot.watchdone.database

object DatabaseFields {
    //table 'users'
    const val COLLECTION_USERS = "users"
    const val COLLECTION_WATCHDONE = "watchdone"
    const val COLLECTION_WATCHLIST = "watchlist"
    const val COLLECTION_ITEMS = "items"
    const val FIELD_NAME = "name"
    const val FIELD_EMAIL = "email"
    const val FIELD_UID = "uid"
    const val FIELD_FCM_ID = "fcmId"
    const val FIELD_TOTAL_ITEMS = "total_items"
    const val FIELD_RELEASE_DATE = "releaseDate"
}