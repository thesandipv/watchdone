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

package com.afterroot.watchdone.data.daos

import com.afterroot.watchdone.data.compoundmodel.EntryWithMedia
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.data.model.PaginatedEntry

interface PaginatedEntryDao<EC : PaginatedEntry, LI : EntryWithMedia<EC>> : EntryDao<EC, LI> {
  suspend fun deletePage(page: Int): Unit = TODO("Not Implemented")
  suspend fun deletePage(page: Int, mediaType: MediaType): Unit = TODO("Not Implemented")
  suspend fun deletePage(
    page: Int,
    mediaType: MediaType,
    ofMedia: Int,
  ): Unit = TODO("Not Implemented")
  suspend fun getLastPage(): Int? = TODO("Not Implemented")
  suspend fun getLastPage(mediaType: MediaType): Int? = TODO("Not Implemented")
  suspend fun getLastPage(mediaType: MediaType, ofMedia: Int): Int? = TODO("Not Implemented")
}

suspend fun <EC : PaginatedEntry, LI : EntryWithMedia<EC>> PaginatedEntryDao<EC, LI>.updatePage(
  page: Int,
  entities: List<EC>,
  mediaType: MediaType? = null,
) {
  if (mediaType == null) {
    deletePage(page)
  } else {
    deletePage(page, mediaType)
  }
  upsertAll(entities)
}

suspend fun <EC : PaginatedEntry, LI : EntryWithMedia<EC>> PaginatedEntryDao<EC, LI>.updatePage(
  page: Int,
  recOf: Int,
  mediaType: MediaType,
  entities: List<EC>,
) {
  deletePage(page, mediaType, recOf)
  upsertAll(entities)
}
