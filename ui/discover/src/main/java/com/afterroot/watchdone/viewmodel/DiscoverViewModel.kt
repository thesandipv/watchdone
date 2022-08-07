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
import com.afterroot.tmdbapi.repository.DiscoverRepository
import com.afterroot.watchdone.base.compose.Actions
import com.afterroot.watchdone.data.mapper.toMulti
import dagger.hilt.android.lifecycle.HiltViewModel
import info.movito.themoviedbapi.model.Discover
import info.movito.themoviedbapi.model.Multi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    val savedState: SavedStateHandle? = null,
    private val discoverRepository: DiscoverRepository
) : ViewModel() {
    private val actions = MutableSharedFlow<DiscoverActions>()
    private val media = MutableSharedFlow<List<Multi>>()

    init {
        Timber.d("init: Start")

        viewModelScope.launch {
            actions.collect { action ->
                when (action) {
                    is DiscoverActions.SetMediaType -> {
                        when (action.mediaType) {
                            Multi.MediaType.MOVIE -> {
                                val list = discoverRepository.getMoviesDiscover(Discover()) // TODO add params also
                                media.emit(list.toMulti())
                            }
                            Multi.MediaType.TV_SERIES -> {
                                val list = discoverRepository.getTVDiscover(Discover()) // TODO add params also
                                media.emit(list.toMulti())
                            }
                            else -> {}
                        }
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

    fun getMedia(): SharedFlow<List<Multi>> = media
}

sealed class DiscoverActions : Actions() {
    data class SetMediaType(val mediaType: Multi.MediaType) : DiscoverActions()
}
