/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
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
) : WDEntity,
  TmdbIdEntity,
  Watchable {
  companion object {
    val EMPTY = Media()
  }
}
