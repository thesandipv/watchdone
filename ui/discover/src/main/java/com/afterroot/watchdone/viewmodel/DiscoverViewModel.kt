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
import app.moviebase.tmdb.discover.DiscoverCategory
import app.moviebase.tmdb.discover.DiscoverCategory.NowPlaying
import app.moviebase.tmdb.discover.DiscoverCategory.Popular
import app.moviebase.tmdb.model.TmdbMediaType
import app.tivi.util.Logger
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.data.repositories.UserDataRepository
import com.afterroot.watchdone.domain.observers.ObservePagedDiscover
import com.afterroot.watchdone.ui.discover.DiscoverLists
import com.afterroot.watchdone.ui.discover.DiscoverViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DiscoverViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  private val userDataRepository: UserDataRepository,
  private val discoverLists: DiscoverLists,
  logger: Logger,
) : ViewModel() {

  val state: StateFlow<DiscoverViewState> = combine(
    savedStateHandle.getStateFlow(KEY_MEDIA_TYPE, MediaType.MOVIE),
  ) {
    DiscoverViewState(it[0])
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = DiscoverViewState.Empty,
  )

  val pagedDiscoverList = discoverLists.pagedPopularList(viewModelScope)
  val pagedNowPlayingList = discoverLists.pagedNowPlayingList(viewModelScope)
  val pagedDiscoverOnTv = discoverLists.pagedOnTVList(viewModelScope)
  val pagedTopRated = discoverLists.pagedTopRatedList(viewModelScope)

  init {
    logger.d { "init: $this" }

    viewModelScope.launch {
      val mediaTypeViews = userDataRepository.userData.mapLatest {
        it.mediaTypeViews
      }.first()?.get(KEY_MEDIA_TYPE) ?: MediaType.MOVIE.name

      setMediaType(MediaType.valueOf(mediaTypeViews), false)
    }
  }

  fun setMediaType(mediaType: MediaType, updateSettings: Boolean = true) {
    savedStateHandle[KEY_MEDIA_TYPE] = mediaType

    discoverLists.popular(discoverParams(mediaType))
    discoverLists.topRated(topRatedParams(mediaType))

    when (mediaType) {
      MediaType.MOVIE -> {
        discoverLists.nowPlaying(nowPlayingParams)
      }

      MediaType.SHOW -> {
        discoverLists.onTV(onTvParams)
      }

      else -> {}
    }

    if (updateSettings) {
      viewModelScope.launch {
        userDataRepository.updateMediaTypeViews(KEY_MEDIA_TYPE, mediaType)
      }
    }
  }

  companion object {
    private val defaultPagingConfig = PagingConfig(20, initialLoadSize = 40)

    fun discoverParams(mediaType: MediaType) = ObservePagedDiscover.Params(
      mediaType = mediaType,
      pagingConfig = defaultPagingConfig,
      category = Popular(TmdbMediaType.valueOf(mediaType.name)),
    )

    fun topRatedParams(mediaType: MediaType) = ObservePagedDiscover.Params(
      mediaType = mediaType,
      pagingConfig = defaultPagingConfig,
      category = DiscoverCategory.TopRated(TmdbMediaType.valueOf(mediaType.name)),
    )

    val nowPlayingParams by lazy {
      ObservePagedDiscover.Params(
        mediaType = MediaType.MOVIE,
        pagingConfig = defaultPagingConfig,
        category = NowPlaying,
      )
    }

    val onTvParams by lazy {
      ObservePagedDiscover.Params(
        mediaType = MediaType.SHOW,
        pagingConfig = defaultPagingConfig,
        category = DiscoverCategory.OnTv,
      )
    }

    const val KEY_MEDIA_TYPE = "media_type"
  }
}
