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

package com.afterroot.watchdone.data.compoundmodel

import androidx.room.Embedded
import androidx.room.Relation
import com.afterroot.watchdone.data.model.Entry
import com.afterroot.watchdone.data.model.Media
import java.util.Objects

open class RoomEntryWithMedia<ET : Entry> : EntryWithMedia<ET> {
  @Embedded
  override lateinit var entry: ET

  @Relation(parentColumn = "media_id", entityColumn = "id")
  override lateinit var relations: List<Media>

  override fun equals(other: Any?): Boolean = when {
    other === this -> true
    other is EntryWithMedia<*> -> {
      entry == other.entry && relations == other.relations
    }

    else -> false
  }

  override fun hashCode(): Int = Objects.hash(entry, relations)
}
