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
import com.afterroot.watchdone.base.Collection
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.Season
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.domain.interactors.TVEpisodeInteractor
import com.afterroot.watchdone.domain.interactors.TVSeasonInteractor
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.media.MediaInfoViewState
import com.afterroot.watchdone.utils.State
import com.afterroot.watchdone.utils.collectionWatchdone
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import info.movito.themoviedbapi.model.Multi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MediaInfoViewModel @Inject constructor(
    val savedState: SavedStateHandle? = null,
    var db: FirebaseFirestore,
    var settings: Settings,
    val tvSeasonInteractor: TVSeasonInteractor,
    val tvEpisodeInteractor: TVEpisodeInteractor
) : ViewModel() {
    private val mediaId = MutableStateFlow(0)
    private val mediaType = MutableStateFlow(Multi.MediaType.MOVIE)
    private val selectedMediaFlow = MutableStateFlow<SelectedMedia>(SelectedMedia.Empty)
    private val selectedSeason = MutableStateFlow(1)
    private val seasonInfo = MutableStateFlow<State<Season>>(State.loading())
    private var watchlistSnapshotFlow = MutableStateFlow<State<QuerySnapshot>>(State.loading())

    fun observeWatchlistSnapshot(
        userId: String,
        isReload: Boolean = false,
        additionQueries: (Query.() -> Query)? = null
    ): StateFlow<State<QuerySnapshot>> {
        watchlistSnapshotFlow.value = State.loading()
        val ref = db.collectionWatchdone(id = userId, settings.isUseProdDb)
            .document(Collection.WATCHLIST)
            .collection(Collection.ITEMS)

        if (additionQueries == null) {
            ref.addSnapshotListener { querySnapshot, _ ->
                querySnapshot?.let {
                    watchlistSnapshotFlow.value = State.success(it)
                }
            }
        } else {
            ref.additionQueries().addSnapshotListener { querySnapshot, _ ->
                querySnapshot?.let {
                    watchlistSnapshotFlow.value = State.success(it)
                }
            }
        }
        return watchlistSnapshotFlow
    }

    fun selectMedia(movie: Movie? = null, tv: TV? = null) {
        movie?.let {
            setMediaType(Multi.MediaType.MOVIE)
            selectedMediaFlow.value = SelectedMedia.Movie(movie)
            mediaId.value = movie.id
        }
        tv?.let {
            setMediaType(Multi.MediaType.TV_SERIES)
            selectedMediaFlow.value = SelectedMedia.TV(tv)
            loadSeason(it.id, selectedSeason.value)
            mediaId.value = tv.id
        }
    }

    fun getSelectedMedia(): StateFlow<SelectedMedia> = selectedMediaFlow

    val state: StateFlow<MediaInfoViewState> =
        combine(
            mediaType,
            selectedMediaFlow,
            seasonInfo,
            selectedSeason
        ) { mediaType, selectedMedia, seasonInfo, selectedSeason ->
            MediaInfoViewState(
                mediaType = mediaType,
                selectedMedia = selectedMedia,
                seasonInfo = seasonInfo,
                selectedSeason = selectedSeason
            ).apply {
                Timber.d("load: State: $this")
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = MediaInfoViewState.Empty
        )

    fun setMediaType(type: Multi.MediaType) {
        mediaType.value = type
    }

    fun loadSeason(id: Int, season: Int) {
        viewModelScope.launch {
            tvSeasonInteractor.executeSync(TVSeasonInteractor.Params(id, season)).collectLatest {
                seasonInfo.value = it
            }
        }
    }

    fun selectSeason(season: Int) {
        viewModelScope.launch {
            selectedSeason.value = season
        }
        loadSeason(mediaId.value, selectedSeason.value)
    }
}

sealed class SelectedMedia {
    data class Movie(val data: com.afterroot.watchdone.data.model.Movie) : SelectedMedia()
    data class TV(val data: com.afterroot.watchdone.data.model.TV) : SelectedMedia()
    object Empty : SelectedMedia()
}
