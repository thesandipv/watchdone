/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.daos

import com.afterroot.watchdone.data.compoundmodel.EntryWithMedia
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.data.model.PaginatedEntry

interface PaginatedEntryDao<EC : PaginatedEntry, LI : EntryWithMedia<EC>> : EntryDao<EC, LI> {
  suspend fun deletePage(page: Int): Unit = TODO("Not Implemented")
  suspend fun deletePage(page: Int, mediaType: MediaType): Unit = TODO("Not Implemented")
  suspend fun deletePage(page: Int, mediaType: MediaType, ofMedia: Int): Unit =
    TODO("Not Implemented")
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
