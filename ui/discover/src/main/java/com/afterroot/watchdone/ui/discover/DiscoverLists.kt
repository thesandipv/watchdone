/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.ui.discover

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.afterroot.watchdone.data.compoundmodel.DiscoverEntryWithMedia
import com.afterroot.watchdone.domain.observers.ObservePagedDiscover
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class DiscoverLists @Inject constructor(observePagedDiscover: Provider<ObservePagedDiscover>) {
  val popular: ObservePagedDiscover by lazy { observePagedDiscover.get() }
  fun pagedPopularList(scope: CoroutineScope): Flow<PagingData<DiscoverEntryWithMedia>> =
    popular.flow.cachedIn(scope)

  val nowPlaying: ObservePagedDiscover by lazy { observePagedDiscover.get() }
  fun pagedNowPlayingList(scope: CoroutineScope): Flow<PagingData<DiscoverEntryWithMedia>> =
    nowPlaying.flow.cachedIn(scope)

  val onTV: ObservePagedDiscover by lazy { observePagedDiscover.get() }
  fun pagedOnTVList(scope: CoroutineScope): Flow<PagingData<DiscoverEntryWithMedia>> =
    onTV.flow.cachedIn(scope)

  val topRated: ObservePagedDiscover by lazy { observePagedDiscover.get() }
  fun pagedTopRatedList(scope: CoroutineScope): Flow<PagingData<DiscoverEntryWithMedia>> =
    topRated.flow.cachedIn(scope)
}
