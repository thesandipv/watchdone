/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.daos

import androidx.paging.PagingSource
import com.afterroot.watchdone.data.compoundmodel.DiscoverEntryWithMedia
import com.afterroot.watchdone.data.model.DiscoverCategory
import com.afterroot.watchdone.data.model.DiscoverEntry
import com.afterroot.watchdone.data.model.MediaType
import kotlinx.coroutines.flow.Flow

interface DiscoverDao : ObservablePaginatedEntryDao<DiscoverEntry, DiscoverEntryWithMedia> {
  fun entriesForPage(
    page: Int,
    mediaType: MediaType,
    category: DiscoverCategory,
  ): Flow<List<DiscoverEntry>>

  fun entriesPagingSource(
    mediaType: MediaType,
    category: DiscoverCategory,
  ): PagingSource<Int, DiscoverEntryWithMedia>

  suspend fun deletePage(page: Int, mediaType: MediaType, category: DiscoverCategory)

  suspend fun updatePage(
    page: Int,
    entities: List<DiscoverEntry>,
    mediaType: MediaType,
    category: DiscoverCategory,
  )
}
