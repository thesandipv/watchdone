/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.compoundmodel

import com.afterroot.watchdone.data.model.Entry
import com.afterroot.watchdone.data.model.Media
import java.util.Objects

interface EntryWithMedia<ET : Entry> {
  var entry: ET
  var relations: List<Media>

  val media: Media
    get() {
      check(relations.size == 1)
      return relations[0]
    }

  fun generateStableId(): Long = Objects.hash(entry::class.java.name, entry.mediaId).toLong()
}
