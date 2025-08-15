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
import com.afterroot.watchdone.data.daos.RecommendedDao
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.media.MediaStore
import com.afterroot.watchdone.media.MediaStoreRequest
import com.afterroot.watchdone.media.recommended.RecommendedMediaStore
import com.afterroot.watchdone.media.recommended.RecommendedMediaStoreKey
import javax.inject.Inject
import kotlinx.coroutines.withContext

class UpdateRecommended @Inject constructor(
  private val recommendedMediaStore: RecommendedMediaStore,
  private val recommendedDao: RecommendedDao,
  private val mediaStore: MediaStore,
  private val dispatchers: CoroutineDispatchers,
  private val logger: Logger,
) : Interactor<UpdateRecommended.Params, Unit>() {
  data class Params(
    val mediaId: Int,
    val page: Int,
    val mediaType: MediaType,
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
          val lastPage = recommendedDao.getLastPage()
          if (lastPage != null) lastPage + 1 else 0
        }

        else -> 1
      }
      logger.d { "APPEND: Fetching page $page" }
      recommendedMediaStore.fetch(
        RecommendedMediaStoreKey(params.mediaId, page, params.mediaType),
        params.forceRefresh,
      ).parallelForEach {
        mediaStore.fetch(MediaStoreRequest(it.mediaId, it.mediaType))
      }
    }
  }
}
