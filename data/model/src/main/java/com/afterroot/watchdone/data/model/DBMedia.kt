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
import info.movito.themoviedbapi.model.Multi

data class DBMedia(
    val id: Int = 0,
    val releaseDate: String? = null,
    val title: String? = null,
    @field:JvmField var isWatched: Boolean? = false,
    var posterPath: String? = null,
    @ServerTimestamp var timestamp: Timestamp = Timestamp.now(),
    var mediaType: Multi.MediaType? = null,
    var rating: Double? = null,
    // TODO update in ards
    @Deprecated("Use watched instead.")
    var watchStatus: Map<String, Boolean> = emptyMap(),
    var watched: List<String> = emptyList(),
) {
    companion object {
        val Empty = DBMedia()
    }
}
