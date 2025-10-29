/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.domain.interactors

import app.tivi.data.util.fetch
import app.tivi.domain.Interactor
import app.tivi.util.Logger
import app.tivi.util.parallelForEach
import com.afterroot.watchdone.base.CoroutineDispatchers
import com.afterroot.watchdone.data.daos.DiscoverDao
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.discover.DiscoverStore
import com.afterroot.watchdone.media.MediaStore
import com.afterroot.watchdone.media.MediaStoreRequest
import javax.inject.Inject
import kotlinx.coroutines.withContext
import app.moviebase.tmdb.discover.DiscoverCategory as TmdbDiscoverCategory

class UpdateDiscover @Inject constructor(
  private val discoverStore: DiscoverStore,
  private val discoverDao: DiscoverDao,
  private val mediaStore: MediaStore,
  private val dispatchers: CoroutineDispatchers,
  private val logger: Logger,
) : Interactor<UpdateDiscover.Params, Unit>() {
  data class Params(
    val mediaType: MediaType,
    val page: Int,
    val category: TmdbDiscoverCategory,
    val forceRefresh: Boolean = false,
  )

  object Page {
    const val NEXT_PAGE = -1
    const val REFRESH = -2
  }

  override suspend fun doWork(params: Params) {
    withContext(dispatchers.io) {
      val page = when {
        params.page >= 0 -> params.page
        params.page == Page.NEXT_PAGE -> {
          val lastPage = discoverDao.getLastPage()
          if (lastPage != null) lastPage + 1 else 0
        }

        else -> 1
      }
      logger.d { "APPEND: Fetching page $page" }
      discoverStore.build().fetch(
        DiscoverStore.DiscoverStoreKey(
          page = page,
          mediaType = params.mediaType,
          category = params.category,
        ),
        params.forceRefresh,
      ).parallelForEach {
        mediaStore.fetch(MediaStoreRequest(it.mediaId, MediaType.SHOW))
      }
    }
  }
}
