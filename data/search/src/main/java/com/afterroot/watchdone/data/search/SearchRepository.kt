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

package com.afterroot.watchdone.data.search

import androidx.collection.LruCache
import app.tivi.data.db.DatabaseTransactionRunner
import com.afterroot.watchdone.base.CoroutineDispatchers
import com.afterroot.watchdone.data.daos.MediaDao
import com.afterroot.watchdone.data.daos.getIdOrSaveMedia
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.search.SearchDataSource.Params
import javax.inject.Inject
import kotlinx.coroutines.withContext

class SearchRepository @Inject constructor(
  private val searchDataSource: SearchDataSource,
  private val mediaDao: MediaDao,
  private val transactionRunner: DatabaseTransactionRunner,
  private val dispatchers: CoroutineDispatchers,
) {

  private data class CacheKey(val page: Int, val query: String)

  private val cache by lazy { LruCache<CacheKey, List<Long>>(20) }

  suspend fun search(params: Params): List<Media> {
    if (params.query.isBlank()) {
      return emptyList()
    }

    val cacheValues = cache[CacheKey(params.page, params.query)]
    if (cacheValues != null) {
      return cacheValues.mapNotNull { mediaDao.getMediaWithId(it) }
    }

    val remote = runCatching {
      searchDataSource.search(params).map { media ->
        withContext(dispatchers.databaseWrite) {
          transactionRunner {
            mediaDao.getIdOrSaveMedia(media)
          }
        }
      }.also { cache.put(CacheKey(params.page, params.query), it) }
        .mapNotNull { mediaDao.getMediaWithId(it) }
    }

    return remote.getOrDefault(emptyList())
  }
}
