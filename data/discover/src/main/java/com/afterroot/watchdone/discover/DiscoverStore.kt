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

package com.afterroot.watchdone.discover

import app.moviebase.tmdb.discover.DiscoverCategory
import app.tivi.data.db.DatabaseTransactionRunner
import app.tivi.util.Logger
import com.afterroot.watchdone.base.CoroutineDispatchers
import com.afterroot.watchdone.data.daos.DiscoverDao
import com.afterroot.watchdone.data.daos.MediaDao
import com.afterroot.watchdone.data.daos.getIdOrSaveMedia
import com.afterroot.watchdone.data.mapper.TmdbDiscoverCategoryToDiscoverCategory
import com.afterroot.watchdone.data.model.DiscoverEntry
import com.afterroot.watchdone.data.model.MediaType
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.StoreBuilder

class DiscoverStore @Inject constructor(
  @Named("tmdbDiscoverDataSource") dataSource: DiscoverDataSource,
  discoverDao: DiscoverDao,
  mediaDao: MediaDao,
  dispatchers: CoroutineDispatchers,
  transactionRunner: DatabaseTransactionRunner,
  categoryMapper: TmdbDiscoverCategoryToDiscoverCategory,
  logger: Logger,
) {
  private val fetcher = Fetcher.of { key: DiscoverStoreKey ->
    dataSource(key.page, key.mediaType, key.category).let { response ->
      logger.d { "DiscoverStore: Fetched for $key" }
      withContext(dispatchers.databaseWrite) {
        transactionRunner {
          response.map { media ->
            DiscoverEntry(
              mediaId = mediaDao.getIdOrSaveMedia(media),
              page = key.page,
              mediaType = media.mediaType ?: MediaType.MOVIE,
              category = categoryMapper.map(key.category),
            )
          }
        }
      }
    }
  }

  private val sot = SourceOfTruth.of<DiscoverStoreKey, List<DiscoverEntry>, List<DiscoverEntry>>(
    reader = { key ->
      discoverDao.entriesForPage(key.page, key.mediaType, categoryMapper.map(key.category))
    },
    writer = { key, response ->
      transactionRunner {
        discoverDao.updatePage(key.page, response, key.mediaType, categoryMapper.map(key.category))
      }
    },
    delete = { discoverDao.deletePage(it.page, it.mediaType, categoryMapper.map(it.category)) },
    deleteAll = discoverDao::deleteAll,
  )

  fun build() = StoreBuilder.from(fetcher, sot).build()

  data class DiscoverStoreKey(
    val mediaType: MediaType,
    val page: Int,
    val category: DiscoverCategory,
  )
}
