/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.discover

import app.moviebase.tmdb.discover.DiscoverCategory
import app.moviebase.tmdb.discover.DiscoverFactory
import app.tivi.data.db.DatabaseTransactionRunner
import app.tivi.util.Logger
import com.afterroot.watchdone.base.CoroutineDispatchers
import com.afterroot.watchdone.data.daos.DiscoverDao
import com.afterroot.watchdone.data.daos.MediaDao
import com.afterroot.watchdone.data.daos.getIdOrSaveMedia
import com.afterroot.watchdone.data.mapper.TmdbDiscoverCategoryToDiscoverCategory
import com.afterroot.watchdone.data.model.DiscoverEntry
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.di.Tmdb
import javax.inject.Inject
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.StoreBuilder

class DiscoverStore @Inject constructor(
  @Tmdb dataSource: DiscoverDataSource,
  discoverDao: DiscoverDao,
  mediaDao: MediaDao,
  dispatchers: CoroutineDispatchers,
  transactionRunner: DatabaseTransactionRunner,
  categoryMapper: TmdbDiscoverCategoryToDiscoverCategory,
  logger: Logger,
) {
  private val fetcher = Fetcher.of { key: DiscoverStoreKey ->
    dataSource(
      key.page,
      key.mediaType,
      DiscoverFactory.createByCategory(key.category),
    ).let { response ->
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
