/*
 * Copyright (C) 2020-2022 Sandip Vaghela
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
package com.afterroot.watchdone.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.afterroot.watchdone.base.compose.Actions
import com.afterroot.watchdone.domain.interactors.GetDiscoverMovies
import com.afterroot.watchdone.domain.interactors.GetDiscoverTV
import com.afterroot.watchdone.domain.observers.DiscoverMoviePagingSource
import com.afterroot.watchdone.domain.observers.DiscoverTVPagingSource
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.discover.DiscoverViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import info.movito.themoviedbapi.model.Discover
import info.movito.themoviedbapi.model.Multi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    val savedState: SavedStateHandle? = null,
    val settings: Settings,
    private val getDiscoverMovies: GetDiscoverMovies,
    private val getDiscoverTV: GetDiscoverTV
) : ViewModel() {
    private val actions = MutableSharedFlow<DiscoverActions>()
    private val mediaType = MutableSharedFlow<Multi.MediaType>()
    private val discover = Discover()

    val state: StateFlow<DiscoverViewState> = combine(mediaType) { it ->
        DiscoverViewState(it[0])
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DiscoverViewState.Empty
    )

    val discoverMovies = Pager(PagingConfig(20, initialLoadSize = 40)) {
        DiscoverMoviePagingSource(discover, getDiscoverMovies)
    }.flow.cachedIn(viewModelScope)

    val discoverTV = Pager(PagingConfig(20, initialLoadSize = 40)) {
        DiscoverTVPagingSource(discover, getDiscoverTV)
    }.flow.cachedIn(viewModelScope)

    init {
        Timber.d("init: Start")

        viewModelScope.launch {
            actions.collect { action ->
                when (action) {
                    is DiscoverActions.SetMediaType -> {
                        mediaType.emit(action.mediaType)
                    }
                }
            }
        }
    }

    internal fun submitAction(action: DiscoverActions) {
        Timber.d("submitAction: Action $action")
        viewModelScope.launch {
            actions.emit(action)
        }
    }

    internal fun getAction() = actions
}

sealed class DiscoverActions : Actions() {
    data class SetMediaType(val mediaType: Multi.MediaType) : DiscoverActions()
}
