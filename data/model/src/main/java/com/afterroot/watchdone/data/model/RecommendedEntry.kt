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
  tableName = "recommended_entries",
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
data class RecommendedEntry(
  @PrimaryKey(autoGenerate = true)
  override val id: Long = 0,
  @ColumnInfo(name = "media_id")
  override val mediaId: Long,
  @ColumnInfo(name = "page")
  override val page: Int,
  @ColumnInfo(name = "media_type")
  val mediaType: MediaType,
  @ColumnInfo(name = "rec_of")
  val recommendationOf: Int,
) : PaginatedEntry
