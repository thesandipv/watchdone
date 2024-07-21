/*
 * Copyright (C) 2020-2024 Sandip Vaghela
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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "media",
  indices = [
    Index(value = ["tmdb_id"], unique = true),
  ],
)
data class Media(
  @PrimaryKey(autoGenerate = true) override val id: Long = 0,
  @ColumnInfo(name = "tmdb_id") override val tmdbId: Int? = null,
  @ColumnInfo(name = "release_date") val releaseDate: String? = null,
  @ColumnInfo(name = "title") val title: String? = null,
  @ColumnInfo(name = "is_watched") override val isWatched: Boolean = false,
  @ColumnInfo(name = "poster_path") val posterPath: String? = null,
  @ColumnInfo(name = "media_type") val mediaType: MediaType? = null,
  @ColumnInfo(name = "rating") val rating: Float? = null,
) : WDEntity, TmdbIdEntity, Watchable {
  companion object {
    val EMPTY = Media()
  }
}
