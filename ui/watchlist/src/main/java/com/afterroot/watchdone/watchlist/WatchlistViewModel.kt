/*
 * Copyright (C) 2020-2023 Sandip Vaghela
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
package com.afterroot.watchdone.watchlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.afterroot.data.utils.FirebaseUtils
import com.afterroot.watchdone.data.WatchlistPagingSource
import com.afterroot.watchdone.data.model.Filters
import com.afterroot.watchdone.settings.Settings
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private var db: FirebaseFirestore,
    var settings: Settings,
    var firebaseUtils: FirebaseUtils
) : ViewModel() {
    private val flowIsLoading = MutableStateFlow(false)
    private val sortAscending = MutableStateFlow(settings.ascSort)
    private val filters = MutableStateFlow(Filters.EMPTY)

    val state: StateFlow<WatchlistState> =
        combine(
            flowIsLoading,
            sortAscending,
            filters
        ) { isLoading, sortAsc, filters ->
            WatchlistState(loading = isLoading, sortAscending = sortAsc, filters = filters)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WatchlistState.Empty
        )

    init {
        Timber.d("init: Initializing")
    }

    fun setSort(ascending: Boolean) {
        settings.ascSort = ascending
        sortAscending.value = ascending
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
            filters.value
        )
    }.flow.cachedIn(viewModelScope)
}
