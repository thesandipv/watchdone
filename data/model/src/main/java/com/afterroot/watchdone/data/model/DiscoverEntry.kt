/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "discover_entries",
  indices = [
    Index(value = ["media_id"], unique = true),
  ],
  foreignKeys = [
    ForeignKey(
      entity = Media::class,
      parentColumns = arrayOf("id"),
      childColumns = arrayOf("media_id"),
      onUpdate = ForeignKey.CASCADE,
      onDelete = ForeignKey.CASCADE,
    ),
  ],
)
data class DiscoverEntry(
  @PrimaryKey(autoGenerate = true)
  override val id: Long = 0,
  @ColumnInfo(name = "media_id")
  override val mediaId: Long,
  @ColumnInfo(name = "page")
  override val page: Int,
  @ColumnInfo(name = "media_type")
  override val mediaType: MediaType,
  @ColumnInfo(name = "discover_category", defaultValue = "UNCATEGORIZED")
  val category: DiscoverCategory,
) : MediaPaginatedEntry

enum class DiscoverCategory {
  UNCATEGORIZED,
  NOW_PLAYING,
  UPCOMING,
  POPULAR,
  TOP_RATED,
  AIRING_TODAY,
  ON_DVD,
  ON_TV,
  ON_NETFLIX,
  ON_AMAZON,
  ON_DISNEY_PLUS,
  ON_APPLE_TV,
}
