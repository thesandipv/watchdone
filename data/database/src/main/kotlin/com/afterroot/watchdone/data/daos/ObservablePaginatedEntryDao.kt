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
