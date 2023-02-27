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
package com.afterroot.watchdone.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.afterroot.tmdbapi.model.Query
import com.afterroot.watchdone.domain.interactors.SearchMovieInteractor
import com.afterroot.watchdone.domain.interactors.SearchTVInteractor
import com.afterroot.watchdone.domain.observers.SearchMoviePagingSource
import com.afterroot.watchdone.domain.observers.SearchTVPagingSource
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.search.SearchViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import info.movito.themoviedbapi.model.Multi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    val savedState: SavedStateHandle? = null,
    val settings: Settings,
    private val searchMovieInteractor: SearchMovieInteractor,
    private val searchTVInteractor: SearchTVInteractor
) : ViewModel() {
    private val mediaType = MutableStateFlow(Multi.MediaType.MOVIE)
    private var searchQuery = MutableStateFlow(Query())
    private var _query = MutableSharedFlow<Query>()
    private var isRefresh = MutableStateFlow(false)
    private var isLoading = MutableStateFlow(false)
    private var isEmpty = MutableStateFlow(true)

    val state: StateFlow<SearchViewState> =
        combine(
            mediaType,
            searchQuery,
            isRefresh,
            isLoading,
            isEmpty
        ) { mediaType, searchQuery, isRefresh, isLoading, isEmpty ->
            SearchViewState(
                mediaType = mediaType,
                query = searchQuery,
                refresh = isRefresh,
                isLoading = isLoading,
                empty = isEmpty
            ).apply {
                Timber.d("load: State: $this")
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SearchViewState.Empty
        )

    val searchMovies = Pager(PagingConfig(20, initialLoadSize = 40)) {
        isRefresh.value = false
        SearchMoviePagingSource(searchQuery.value, searchMovieInteractor)
    }.flow.cachedIn(viewModelScope)

    val searchTV = Pager(PagingConfig(20, initialLoadSize = 40)) {
        isRefresh.value = false
        SearchTVPagingSource(searchQuery.value, searchTVInteractor)
    }.flow.cachedIn(viewModelScope)

    init {
        Timber.d("init: Start")

        viewModelScope.launch {
            _query.debounce(300).collectLatest {
                searchQuery.value = it
                isRefresh.value = true
            }
        }
    }

    fun search(query: Query) {
        viewModelScope.launch {
            _query.emit(query)
        }
    }

    fun setMediaType(type: Multi.MediaType) {
        mediaType.value = type
        isRefresh.value = true
    }

    fun setLoading(loading: Boolean) {
        isLoading.value = loading
    }

    fun setEmpty(empty: Boolean) {
        isEmpty.value = empty
    }
}
