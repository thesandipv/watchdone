/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
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
