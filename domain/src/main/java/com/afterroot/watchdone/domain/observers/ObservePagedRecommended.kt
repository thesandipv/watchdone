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
