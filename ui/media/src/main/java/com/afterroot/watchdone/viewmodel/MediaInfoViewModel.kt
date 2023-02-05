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
import app.tivi.extensions.combine
import com.afterroot.watchdone.data.model.DBMedia
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.domain.interactors.MediaInfoInteractor
import com.afterroot.watchdone.domain.interactors.ObserveMediaInfo
import com.afterroot.watchdone.domain.interactors.ObserveMovieCredits
import com.afterroot.watchdone.domain.interactors.ObserveMovieInfo
import com.afterroot.watchdone.domain.interactors.ObserveRecommendedMovies
import com.afterroot.watchdone.domain.interactors.ObserveRecommendedShows
import com.afterroot.watchdone.domain.interactors.ObserveTVCredits
import com.afterroot.watchdone.domain.interactors.ObserveTVInfo
import com.afterroot.watchdone.domain.interactors.ObserveTVSeason
import com.afterroot.watchdone.domain.interactors.TVEpisodeInteractor
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MediaInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeMovieInfo: ObserveMovieInfo,
    observeTVInfo: ObserveTVInfo,
    observeMovieCredits: ObserveMovieCredits,
    observeTVCredits: ObserveTVCredits,
    observeTVSeason: ObserveTVSeason,
    private val observeMediaInfo: ObserveMediaInfo,
    private val observeRecommendedMovies: ObserveRecommendedMovies,
    private val observeRecommendedShows: ObserveRecommendedShows,
    private val watchlistInteractor: WatchlistInteractor,
    private val watchStateInteractor: WatchStateInteractor,
    private val tvEpisodeInteractor: TVEpisodeInteractor,
    private val mediaInfoInteractor: MediaInfoInteractor
) : ViewModel() {

    private val mediaId = savedStateHandle.getStateFlow("mediaId", 0)
    private val _mediaType = savedStateHandle.getStateFlow("type", "")

    val mediaType = Multi.MediaType.valueOf(_mediaType.value.uppercase())

    private val isInWL = MutableStateFlow(false)
    private val isWatched = MutableStateFlow(false)
    private val selectedSeason = MutableStateFlow(1)
    private val dbMedia = MutableStateFlow(DBMedia.Empty)

    private val stateMovie: StateFlow<MediaInfoViewState> by lazy {
        combine(
            mediaId,
            isInWL,
            isWatched,
            observeMovieInfo.flow,
            observeMovieCredits.flow,
            dbMedia
        ) { mediaId, isInWL, isWatched, movieInfo, credits, mediaInfo ->
            MediaInfoViewState(
                mediaId = mediaId,
                mediaType = mediaType,
                movie = if (movieInfo is State.Success) movieInfo.data else Movie.Empty,
                isInWatchlist = isInWL,
                isWatched = isWatched,
                credits = credits,
                media = mediaInfo
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = MediaInfoViewState.Empty
        )
    }

    private val stateTV: StateFlow<MediaInfoViewState> by lazy {
        combine(
            mediaId,
            isInWL,
            isWatched,
            observeTVInfo.flow,
            observeTVCredits.flow,
            observeTVSeason.flow,
            dbMedia
        ) { mediaId, isInWL, isWatched, tvInfo, credits, season, mediaInfo ->
            MediaInfoViewState(
                mediaId = mediaId,
                mediaType = mediaType,
                tv = if (tvInfo is State.Success) tvInfo.data else TV.Empty,
                isInWatchlist = isInWL,
                isWatched = isWatched,
                credits = credits,
                seasonInfo = season,
                media = mediaInfo
            )
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
            observeMovieCredits(ObserveMovieCredits.Params(mediaId.value))
        } else if (mediaType == Multi.MediaType.TV_SERIES) {
            observeTVInfo(ObserveTVInfo.Params(mediaId.value))
            observeTVCredits(ObserveTVCredits.Params(mediaId.value))

            selectedSeason.onEach {
                observeTVSeason(ObserveTVSeason.Params(mediaId.value, it))
            }.launchIn(viewModelScope)
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

        getMediaInfo()
    }

    fun getRecommendedShows(mediaId: Int) = Pager(PagingConfig(pageSize = 20, initialLoadSize = 20)) {
        RecommendedShowPagingSource(mediaId, observeRecommendedShows)
    }.flow.cachedIn(viewModelScope)

    fun getRecommendedMovies(mediaId: Int) = Pager(PagingConfig(pageSize = 20, initialLoadSize = 20)) {
        RecommendedMoviePagingSource(mediaId, observeRecommendedMovies)
    }.flow.cachedIn(viewModelScope)

    fun watchlistAction(isAdd: Boolean, media: DBMedia) {
        viewModelScope.launch {
            watchlistInteractor.executeSync(
                WatchlistInteractor.Params(
                    mediaId.value,
                    media,
                    if (isAdd) WatchlistInteractor.Method.ADD else WatchlistInteractor.Method.REMOVE
                )
            ).collect { result ->
                result.whenSuccess {
                    isInWL.value = isAdd
                    if (!isAdd) { // Set watched to false when media removed from watchlist
                        isWatched.value = false
                    }
                }
            }
        }
    }

    fun watchStateAction(isMark: Boolean, media: DBMedia) {
        viewModelScope.launch {
            watchStateInteractor.executeSync(
                WatchStateInteractor.Params(
                    id = mediaId.value,
                    watchState = isMark,
                    method = WatchStateInteractor.Method.MEDIA
                )
            ).collect { result ->
                result.whenSuccess {
                    isWatched.value = it
                }.whenFailed { message, exception ->
                    Timber.e(exception, "watchStateAction: $message")
                }
            }
        }
    }

    fun episodeWatchStateAction(episodeId: String, isMark: Boolean) {
        viewModelScope.launch {
            watchStateInteractor.executeSync(
                WatchStateInteractor.Params(
                    id = mediaId.value,
                    watchState = isMark,
                    episodeId = episodeId,
                    method = WatchStateInteractor.Method.EPISODE
                )
            ).collect { result ->
                result.whenSuccess {
                    // TODO this is costly
                    getMediaInfo()
                }
            }
        }
    }

    private fun getMediaInfo() {
        viewModelScope.launch {
            mediaInfoInteractor.executeSync(MediaInfoInteractor.Params(mediaId.value)).collectLatest { result ->
                result.whenSuccess {
                    dbMedia.value = it
                    isWatched.value = it.isWatched ?: false
                }
            }
        }
    }

    fun selectSeason(season: Int) {
        selectedSeason.value = season
    }
}
