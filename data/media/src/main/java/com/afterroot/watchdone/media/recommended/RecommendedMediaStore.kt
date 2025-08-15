/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.media.recommended

import app.tivi.data.db.DatabaseTransactionRunner
import app.tivi.data.util.storeBuilder
import com.afterroot.watchdone.base.CoroutineDispatchers
import com.afterroot.watchdone.data.daos.MediaDao
import com.afterroot.watchdone.data.daos.RecommendedDao
import com.afterroot.watchdone.data.daos.getIdOrSaveMedia
import com.afterroot.watchdone.data.daos.updatePage
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.data.model.RecommendedEntry
import javax.inject.Inject
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store

class RecommendedMediaStore @Inject constructor(
  dataSource: RecommendedDataSource,
  recommendedDao: RecommendedDao,
  mediaDao: MediaDao,
  dispatchers: CoroutineDispatchers,
  transactionRunner: DatabaseTransactionRunner,
) : Store<RecommendedMediaStoreKey, List<RecommendedEntry>> by storeBuilder(
  fetcher = Fetcher.of { key: RecommendedMediaStoreKey ->
    dataSource(key.mediaId, key.mediaType, key.page).let { response ->
      withContext(dispatchers.databaseWrite) {
        transactionRunner {
          response.map { media ->
            RecommendedEntry(
              mediaId = mediaDao.getIdOrSaveMedia(media),
              page = key.page,
              mediaType = media.mediaType ?: MediaType.MOVIE,
              recommendationOf = key.mediaId,
            )
          }
        }
      }
    }
  },
  sourceOfTruth = SourceOfTruth.of(
    reader = { key ->
      recommendedDao.entriesForPage(key.page, key.mediaId)
    },
    writer = { key, response ->
      transactionRunner {
        recommendedDao.updatePage(key.page, key.mediaId, key.mediaType, response)
      }
    },
    delete = { key ->
      recommendedDao.deletePage(key.page, key.mediaType, key.mediaId)
    },
    deleteAll = { recommendedDao.deleteAll() },
  ),
).build()

data class RecommendedMediaStoreKey(val mediaId: Int, val page: Int, val mediaType: MediaType)
