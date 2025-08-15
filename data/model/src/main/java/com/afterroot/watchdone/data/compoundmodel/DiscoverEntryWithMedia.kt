/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.compoundmodel

import androidx.room.Embedded
import androidx.room.Relation
import com.afterroot.watchdone.data.model.DiscoverEntry
import com.afterroot.watchdone.data.model.Media
import java.util.Objects

class DiscoverEntryWithMedia : EntryWithMedia<DiscoverEntry> {
  @Embedded
  override lateinit var entry: DiscoverEntry

  @Relation(parentColumn = "media_id", entityColumn = "id")
  override lateinit var relations: List<Media>

  override fun equals(other: Any?): Boolean = when {
    other === this -> true
    other is DiscoverEntryWithMedia -> {
      entry == other.entry && relations == other.relations
    }

    else -> false
  }

  override fun hashCode(): Int = Objects.hash(entry, relations)
}
