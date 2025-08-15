/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.watchlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import app.tivi.api.UiMessageManager
import com.afterroot.data.utils.FirebaseUtils
import com.afterroot.watchdone.base.WatchlistType
import com.afterroot.watchdone.data.model.Filters
import com.afterroot.watchdone.domain.observers.WatchlistPagingSource
import com.afterroot.watchdone.settings.Settings
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class WatchlistViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  private var db: FirebaseFirestore,
  var settings: Settings,
  var firebaseUtils: FirebaseUtils,
) : ViewModel() {
  private val flowIsLoading = MutableStateFlow(false)
  private val sortAscending = MutableStateFlow(settings.ascSort)
  private val filters = MutableStateFlow(Filters.EMPTY)
  private val watchlistType = MutableStateFlow(settings.watchlistType)

  private val uiMessageManager = UiMessageManager()

  val state: StateFlow<WatchlistState> =
    combine(
      flowIsLoading,
      sortAscending,
      filters,
      watchlistType,
      uiMessageManager.message,
    ) { isLoading, sortAsc, filters, watchlistType, _ ->
      WatchlistState(
        loading = isLoading,
        sortAscending = sortAsc,
        filters = filters,
        watchlistType = watchlistType,
      )
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = WatchlistState.Empty,
    )

  init {
    Timber.d("init: Initializing")
  }

  fun setSort(ascending: Boolean) {
    settings.ascSort = ascending
    sortAscending.value = ascending
  }

  fun setWatchlistType(type: WatchlistType) {
    settings.watchlistType = type
    watchlistType.value = type
  }

  fun updateFilters(filterUpdates: Filters) {
    viewModelScope.launch {
      filters.emit(filterUpdates)
    }
  }

  val watchlist = Pager(PagingConfig(20)) {
    WatchlistPagingSource(
      db,
      settings,
      firebaseUtils,
      filters.value,
    )
  }.flow.cachedIn(viewModelScope)
}
