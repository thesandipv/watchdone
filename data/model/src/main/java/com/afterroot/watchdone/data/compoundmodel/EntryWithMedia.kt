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

  fun generateStableId(): Long {
    return Objects.hash(entry::class.java.name, entry.mediaId).toLong()
  }
}
