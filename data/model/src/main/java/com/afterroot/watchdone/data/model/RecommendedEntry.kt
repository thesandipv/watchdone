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
