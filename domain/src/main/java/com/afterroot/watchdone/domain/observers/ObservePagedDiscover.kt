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
import com.afterroot.watchdone.data.compoundmodel.DiscoverEntryWithMedia
import com.afterroot.watchdone.data.daos.DiscoverDao
import com.afterroot.watchdone.data.mapper.TmdbDiscoverCategoryToDiscoverCategory
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.domain.interactors.UpdateDiscover
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import app.moviebase.tmdb.discover.DiscoverCategory as TmdbDiscoverCategory

class ObservePagedDiscover @Inject constructor(
  private val discoverDao: DiscoverDao,
  private val updateDiscover: UpdateDiscover,
  private val categoryMapper: TmdbDiscoverCategoryToDiscoverCategory,
  private val logger: Logger,
) : PagingInteractor<ObservePagedDiscover.Params, DiscoverEntryWithMedia>() {

  data class Params(
    val mediaType: MediaType,
    val category: TmdbDiscoverCategory,
    override val pagingConfig: PagingConfig,
  ) : Parameters<DiscoverEntryWithMedia>

  @OptIn(ExperimentalPagingApi::class)
  override suspend fun createObservable(params: Params): Flow<PagingData<DiscoverEntryWithMedia>> =
    Pager(
      config = params.pagingConfig,
      remoteMediator = PaginatedEntryRemoteMediator { page ->
        try {
          logger.d { "APPEND: Requesting Page: $page" }
          updateDiscover(UpdateDiscover.Params(params.mediaType, page, params.category, true))
        } catch (ce: CancellationException) {
          throw ce
        } catch (t: Throwable) {
          logger.e(t) { "Error while fetching from RemoteMediator" }
          throw t
        }
      },
      pagingSourceFactory = {
        discoverDao.entriesPagingSource(params.mediaType, categoryMapper.map(params.category))
      },
    ).flow
}
