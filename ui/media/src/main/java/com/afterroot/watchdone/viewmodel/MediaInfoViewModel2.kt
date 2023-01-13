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
import com.afterroot.watchdone.data.model.DBMedia
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.domain.interactors.ObserveMovieInfo
import com.afterroot.watchdone.domain.interactors.ObserveRecommendedMovies
import com.afterroot.watchdone.domain.interactors.ObserveRecommendedShows
import com.afterroot.watchdone.domain.interactors.ObserveTVInfo
import com.afterroot.watchdone.domain.interactors.WatchStateInteractor
import com.afterroot.watchdone.domain.interactors.WatchlistInteractor
import com.afterroot.watchdone.domain.observers.RecommendedMoviePagingSource
import com.afterroot.watchdone.domain.observers.RecommendedShowPagingSource
import com.afterroot.watchdone.ui.media.MediaInfoViewState
import com.afterroot.watchdone.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import info.movito.themoviedbapi.model.Multi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MediaInfoViewModel2 @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeMovieInfo: ObserveMovieInfo,
    observeTVInfo: ObserveTVInfo,
    private val observeRecommendedMovies: ObserveRecommendedMovies,
    private val observeRecommendedShows: ObserveRecommendedShows,
    private val watchlistInteractor: WatchlistInteractor,
    private val watchStateInteractor: WatchStateInteractor
) : ViewModel() {

    private val mediaId = savedStateHandle.getStateFlow("mediaId", 0)
    private val _mediaType = savedStateHandle.getStateFlow("type", "")

    val mediaType = Multi.MediaType.valueOf(_mediaType.value.uppercase())

    private val isInWL = MutableStateFlow(false)
    private val isWatched = MutableStateFlow(false)

    private val stateMovie: StateFlow<MediaInfoViewState> by lazy {
        combine(mediaId, isInWL, isWatched, observeMovieInfo.flow) { mediaId, isInWL, isWatched, movieInfo ->
            MediaInfoViewState(
                mediaId = mediaId,
                mediaType = mediaType,
                movie = if (movieInfo is State.Success) movieInfo.data else Movie.Empty,
                isInWatchlist = isInWL,
                isWatched = isWatched
            ).apply {
                Timber.d("State: $this")
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = MediaInfoViewState.Empty
        )
    }

    private val stateTV: StateFlow<MediaInfoViewState> by lazy {
        combine(mediaId, isInWL, isWatched, observeTVInfo.flow) { mediaId, isInWL, isWatched, tvInfo ->
            MediaInfoViewState(
                mediaId = mediaId,
                mediaType = mediaType,
                tv = if (tvInfo is State.Success) tvInfo.data else TV.Empty,
                isInWatchlist = isInWL,
                isWatched = isWatched
            ).apply {
                Timber.d("State: $this")
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = MediaInfoViewState.Empty
        )
    }

    val state: StateFlow<MediaInfoViewState> = when (mediaType) {
        Multi.MediaType.MOVIE -> {
            stateMovie
        }
        Multi.MediaType.TV_SERIES -> {
            stateTV
        }
        else -> {
            flow {
                emit(MediaInfoViewState.Empty)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = MediaInfoViewState.Empty
            )
        }
    }

    init {
        if (mediaType == Multi.MediaType.MOVIE) {
            observeMovieInfo(ObserveMovieInfo.Params(mediaId.value))
        } else if (mediaType == Multi.MediaType.TV_SERIES) {
            observeTVInfo(ObserveTVInfo.Params(mediaId.value))
        }
        viewModelScope.launch {
            watchlistInteractor.executeSync(
                WatchlistInteractor.Params(
                    mediaId.value,
                    method = WatchlistInteractor.Method.EXIST
                )
            ).collectLatest {
                if (it is State.Success) {
                    isInWL.value = it.data
                }
            }
        }
    }

    fun getRecommendedShows(mediaId: Int) = Pager(PagingConfig(pageSize = 20, initialLoadSize = 20)) {
        RecommendedShowPagingSource(mediaId, observeRecommendedShows)
    }.flow.cachedIn(viewModelScope)

    fun getRecommendedMovies(mediaId: Int) = Pager(PagingConfig(pageSize = 20, initialLoadSize = 20)) {
        RecommendedMoviePagingSource(mediaId, observeRecommendedMovies)
    }.flow.cachedIn(viewModelScope)

    fun watchlistAction(isAdd: Boolean, media: DBMedia) {
        viewModelScope.launch {
            val task = watchlistInteractor.executeSync(
                WatchlistInteractor.Params(
                    mediaId.value,
                    media,
                    if (isAdd) WatchlistInteractor.Method.ADD else WatchlistInteractor.Method.REMOVE
                )
            )
            task.collect { result ->
                result.whenSuccess {
                    isInWL.value = isAdd
                }
            }
        }
    }

    fun watchStateAction(isMark: Boolean, media: DBMedia) {
        viewModelScope.launch {
            val task = watchStateInteractor.executeSync(
                WatchStateInteractor.Params(
                    mediaId.value,
                    isMark
                )
            )
            task.collect { result ->
                result.whenSuccess {
                    isWatched.value = it
                }.whenFailed { message, exception ->
                    Timber.e(exception, "watchStateAction: $message")
                }
            }
        }
    }
}
