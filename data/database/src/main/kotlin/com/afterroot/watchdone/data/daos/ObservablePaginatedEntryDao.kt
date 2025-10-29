/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.daos

import androidx.paging.PagingSource
import com.afterroot.watchdone.data.compoundmodel.EntryWithMedia
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.data.model.PaginatedEntry
import kotlinx.coroutines.flow.Flow

interface ObservablePaginatedEntryDao<EC : PaginatedEntry, LI : EntryWithMedia<EC>> :
  PaginatedEntryDao<EC, LI> {
  fun entriesForPage(page: Int): Flow<List<EC>>
  fun entriesObservable(count: Int, offset: Int): Flow<List<LI>>
  fun entriesPagingSource(mediaType: MediaType): PagingSource<Int, LI>
  override suspend fun deleteAll(mediaType: MediaType)
  override suspend fun deletePage(page: Int, mediaType: MediaType)
  override suspend fun getLastPage(mediaType: MediaType): Int?
}
