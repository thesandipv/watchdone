/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.domain.observers

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import app.tivi.domain.PaginatedEntryRemoteMediator
import app.tivi.domain.PagingInteractor
import app.tivi.util.Logger
import com.afterroot.watchdone.data.compoundmodel.RecommendedEntryWithMedia
import com.afterroot.watchdone.data.daos.RecommendedDao
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.domain.interactors.UpdateRecommended
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

class ObservePagedRecommended @Inject constructor(
  private val recommendedDao: RecommendedDao,
  private val updateRecommended: UpdateRecommended,
  private val logger: Logger,
) : PagingInteractor<ObservePagedRecommended.Params, RecommendedEntryWithMedia>() {

  data class Params(
    val mediaId: Int,
    val mediaType: MediaType,
    override val pagingConfig: PagingConfig,
  ) : Parameters<RecommendedEntryWithMedia>

  @OptIn(ExperimentalPagingApi::class)
  override suspend fun createObservable(
    params: Params,
  ): Flow<PagingData<RecommendedEntryWithMedia>> = Pager(
    config = params.pagingConfig,
    remoteMediator = PaginatedEntryRemoteMediator { page ->
      try {
        logger.d { "APPEND: Requesting Page: $page" }
        updateRecommended(
          UpdateRecommended.Params(params.mediaId, page, params.mediaType, true),
        )
      } catch (ce: CancellationException) {
        throw ce
      } catch (t: Throwable) {
        logger.e(t) { "Error while fetching from RemoteMediator" }
        throw t
      }
    },
    pagingSourceFactory = {
      recommendedDao.entriesPagingSource(
        params.mediaId,
        params.mediaType,
      )
    },
  ).flow
}
