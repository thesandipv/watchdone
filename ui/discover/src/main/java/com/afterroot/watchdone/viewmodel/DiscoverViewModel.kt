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
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.tivi.util.Logger
import com.afterroot.watchdone.data.compoundmodel.DiscoverEntryWithMedia
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.data.repositories.UserDataRepository
import com.afterroot.watchdone.domain.observers.ObservePagedDiscover
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.discover.DiscoverViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class DiscoverViewModel @Inject constructor(
  val savedStateHandle: SavedStateHandle,
  settings: Settings,
  val observePagedDiscover: ObservePagedDiscover,
  val userDataRepository: UserDataRepository,
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

  val pagedDiscoverList: Flow<PagingData<DiscoverEntryWithMedia>> = observePagedDiscover.flow.cachedIn(
    viewModelScope,
  )

  init {
    logger.d { "init: $this" }

    viewModelScope.launch {
      val mediaTypeViews = userDataRepository.userData.mapLatest {
        it.mediaTypeViews
      }.first()?.get(KEY_MEDIA_TYPE) ?: MediaType.MOVIE.name

      logger.d { "Discover: mediaTypeViews $mediaTypeViews" }

      setMediaType(MediaType.valueOf(mediaTypeViews), false)
    }
  }

  fun setMediaType(mediaType: MediaType, updateSettings: Boolean = true) {
    savedStateHandle[KEY_MEDIA_TYPE] = mediaType
    observePagedDiscover(discoverParams(mediaType))
    if (updateSettings) {
      viewModelScope.launch {
        userDataRepository.updateMediaTypeViews(KEY_MEDIA_TYPE, mediaType)
      }
    }
  }

  companion object {
    fun discoverParams(mediaType: MediaType) = ObservePagedDiscover.Params(
      mediaType = mediaType,
      pagingConfig = PagingConfig(20, initialLoadSize = 40),
    )

    const val KEY_MEDIA_TYPE = "media_type"
  }
}
