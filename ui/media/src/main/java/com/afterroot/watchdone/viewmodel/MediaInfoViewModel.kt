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

package com.afterroot.watchdone.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.tivi.extensions.combine
import app.tivi.util.Logger
import com.afterroot.watchdone.data.compoundmodel.RecommendedEntryWithMedia
import com.afterroot.watchdone.data.model.DBMedia
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.domain.interactors.MediaInfoInteractor
import com.afterroot.watchdone.domain.interactors.ObserveMediaInfo
import com.afterroot.watchdone.domain.interactors.ObserveMovieCredits
import com.afterroot.watchdone.domain.interactors.ObserveMovieInfo
import com.afterroot.watchdone.domain.interactors.ObserveMovieWatchProviders
import com.afterroot.watchdone.domain.interactors.ObserveTVCredits
import com.afterroot.watchdone.domain.interactors.ObserveTVInfo
import com.afterroot.watchdone.domain.interactors.ObserveTVSeason
import com.afterroot.watchdone.domain.interactors.ObserveTVWatchProviders
import com.afterroot.watchdone.domain.interactors.TVEpisodeInteractor
import com.afterroot.watchdone.domain.interactors.WatchStateInteractor
import com.afterroot.watchdone.domain.interactors.WatchlistInteractor
import com.afterroot.watchdone.domain.observers.ObservePagedRecommended
import com.afterroot.watchdone.ui.media.MediaInfoViewState
import com.afterroot.watchdone.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MediaInfoViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  observeMovieInfo: ObserveMovieInfo,
  observeTVInfo: ObserveTVInfo,
  observeMovieCredits: ObserveMovieCredits,
  observeTVCredits: ObserveTVCredits,
  observeTVSeason: ObserveTVSeason,
  observeMovieWatchProviders: ObserveMovieWatchProviders,
  observeTVWatchProviders: ObserveTVWatchProviders,
  observePagedRecommended: ObservePagedRecommended,
  private val logger: Logger,
  private val observeMediaInfo: ObserveMediaInfo,
  private val watchlistInteractor: WatchlistInteractor,
  private val watchStateInteractor: WatchStateInteractor,
  private val tvEpisodeInteractor: TVEpisodeInteractor,
  private val mediaInfoInteractor: MediaInfoInteractor,
) : ViewModel() {

  private val mediaId = savedStateHandle.getStateFlow("mediaId", 0)
  private val _mediaType = savedStateHandle.getStateFlow("type", "")

  val mediaType = MediaType.valueOf(_mediaType.value.uppercase())

  private val isInWL: MutableStateFlow<State<Boolean>> = MutableStateFlow(State.loading())
  private val isWatched: MutableStateFlow<State<Boolean>> = MutableStateFlow(State.loading())
  private val selectedSeason = MutableStateFlow(1)
  private val dbMedia = MutableStateFlow(DBMedia.Empty)

  private val stateMovie: StateFlow<MediaInfoViewState> by lazy {
    combine(
      mediaId,
      isInWL,
      isWatched,
      observeMovieInfo.flow,
      observeMovieCredits.flow,
      observeMovieWatchProviders.flow,
      dbMedia,
    ) { mediaId, isInWL, isWatched, movieInfo, credits, watchProviders, mediaInfo ->
      MediaInfoViewState(
        mediaId = mediaId,
        mediaType = mediaType,
        movie = if (movieInfo is State.Success) movieInfo.data else Movie.Empty,
        isInWatchlist = isInWL,
        isWatched = isWatched,
        credits = credits,
        media = mediaInfo,
        watchProviders = watchProviders,
      )
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(),
      initialValue = MediaInfoViewState.Empty,
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
      observeTVWatchProviders.flow,
      dbMedia,
    ) { mediaId, isInWL, isWatched, tvInfo, credits, season, watchProviders, mediaInfo ->
      MediaInfoViewState(
        mediaId = mediaId,
        mediaType = mediaType,
        tv = if (tvInfo is State.Success) tvInfo.data else TV.Empty,
        isInWatchlist = isInWL,
        isWatched = isWatched,
        credits = credits,
        seasonInfo = season,
        media = mediaInfo,
        watchProviders = watchProviders,
      )
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(),
      initialValue = MediaInfoViewState.Empty,
    )
  }

  val state: StateFlow<MediaInfoViewState> = when (mediaType) {
    MediaType.MOVIE -> stateMovie
    MediaType.SHOW -> stateTV
    else -> flow {
      emit(MediaInfoViewState.Empty)
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(),
      initialValue = MediaInfoViewState.Empty,
    )
  }

  init {
    if (mediaType == MediaType.MOVIE) {
      observeMovieInfo(ObserveMovieInfo.Params(mediaId.value))
      observeMovieCredits(ObserveMovieCredits.Params(mediaId.value))
      observeMovieWatchProviders(ObserveMovieWatchProviders.Params(mediaId.value))
    } else if (mediaType == MediaType.SHOW) {
      observeTVInfo(ObserveTVInfo.Params(mediaId.value))
      observeTVCredits(ObserveTVCredits.Params(mediaId.value))
      observeTVWatchProviders(ObserveTVWatchProviders.Params(mediaId.value))

      selectedSeason.onEach {
        observeTVSeason(ObserveTVSeason.Params(mediaId.value, it))
      }.launchIn(viewModelScope)
    }
    viewModelScope.launch {
      watchlistInteractor.executeSync(
        WatchlistInteractor.Params(
          mediaId.value,
          method = WatchlistInteractor.Method.EXIST,
        ),
      ).collectLatest {
        isInWL.value = it
      }
    }

    getMediaInfo()

    observePagedRecommended(recommendedParams(mediaId.value, mediaType))
  }

  val pagedRecommendedList: Flow<PagingData<RecommendedEntryWithMedia>> = observePagedRecommended.flow.cachedIn(
    viewModelScope,
  )

  fun watchlistAction(isAdd: Boolean, media: DBMedia) {
    viewModelScope.launch {
      watchlistInteractor.executeSync(
        WatchlistInteractor.Params(
          mediaId.value,
          media,
          if (isAdd) WatchlistInteractor.Method.ADD else WatchlistInteractor.Method.REMOVE,
        ),
      ).collect { result ->
        result.whenSuccess {
          isInWL.value = State.success(isAdd)
          if (!isAdd) { // Set watched to false when media removed from watchlist
            isWatched.value = State.success(false)
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
          method = WatchStateInteractor.Method.MEDIA,
        ),
      ).collect { result ->
        result.whenSuccess {
          isWatched.value = result
        }.whenFailed { message, exception ->
          logger.e(exception) { "watchStateAction: $message" }
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
          method = WatchStateInteractor.Method.EPISODE,
        ),
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
      mediaInfoInteractor.executeSync(
        MediaInfoInteractor.Params(mediaId.value),
      ).collectLatest { result ->
        result.whenSuccess {
          dbMedia.value = it
          isWatched.value = State.success(it.isWatched)
        }
      }
    }
  }

  fun selectSeason(season: Int) {
    selectedSeason.value = season
  }

  companion object {
    fun recommendedParams(mediaId: Int, mediaType: MediaType) = ObservePagedRecommended.Params(
      mediaId,
      mediaType,
      PagingConfig(20, initialLoadSize = 20),
    )
  }
}
