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
package com.afterroot.watchdone.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class DBMedia(
    val id: Int = 0,
    val releaseDate: String? = null,
    val title: String? = null,
    @field:JvmField
    val isWatched: Boolean = false,
    val posterPath: String? = null,
    @ServerTimestamp
    val timestamp: Timestamp = Timestamp.now(),
    val mediaType: MediaType? = null,
    val rating: Float? = null,
    val watched: List<String> = emptyList(),
) {
    companion object {
        val Empty = DBMedia()
    }
}
