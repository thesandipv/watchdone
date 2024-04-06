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
