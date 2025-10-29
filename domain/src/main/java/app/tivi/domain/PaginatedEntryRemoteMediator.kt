/*
 * Copyright 2023, Google LLC, Christopher Banes and the Tivi project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package app.tivi.domain

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.afterroot.watchdone.data.compoundmodel.EntryWithMedia
import com.afterroot.watchdone.data.model.PaginatedEntry

/**
 * A [RemoteMediator] which works on [PaginatedEntry] entities. [fetch] will be called with the
 * next page to load.
 */

@OptIn(ExperimentalPagingApi::class)
internal class PaginatedEntryRemoteMediator<LI, ET>(
  private val fetch: suspend (page: Int) -> Unit,
) : RemoteMediator<Int, LI>() where ET : PaginatedEntry, LI : EntryWithMedia<ET> {
  override suspend fun load(loadType: LoadType, state: PagingState<Int, LI>): MediatorResult {
    val nextPage = when (loadType) {
      LoadType.REFRESH -> 1
      LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
      LoadType.APPEND -> {
        val lastItem = state.lastItemOrNull()
          ?: return MediatorResult.Success(endOfPaginationReached = true)
        lastItem.entry.page + 1
      }
    }
    return try {
      fetch(nextPage)
      MediatorResult.Success(endOfPaginationReached = false)
    } catch (t: Throwable) {
      MediatorResult.Error(t)
    }
  }
}
