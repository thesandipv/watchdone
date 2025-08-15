/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.daos

import androidx.paging.PagingSource
import com.afterroot.watchdone.data.compoundmodel.RecommendedEntryWithMedia
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.data.model.RecommendedEntry
import kotlinx.coroutines.flow.Flow

interface RecommendedDao : PaginatedEntryDao<RecommendedEntry, RecommendedEntryWithMedia> {
  fun entriesForPage(page: Int, recOf: Int): Flow<List<RecommendedEntry>>
  fun entriesPagingSource(
    recOf: Int,
    mediaType: MediaType,
  ): PagingSource<Int, RecommendedEntryWithMedia>
}
