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
import com.afterroot.data.utils.FirebaseUtils
import com.afterroot.watchdone.base.Collection
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.Season
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.domain.interactors.MovieCreditsInteractor
import com.afterroot.watchdone.domain.interactors.TVCreditsInteractor
import com.afterroot.watchdone.domain.interactors.TVEpisodeInteractor
import com.afterroot.watchdone.domain.interactors.TVSeasonInteractor
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.media.MediaInfoViewState
import com.afterroot.watchdone.utils.State
import com.afterroot.watchdone.utils.collectionWatchdone
import com.afterroot.watchdone.utils.collectionWatchlistItems
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import info.movito.themoviedbapi.model.Credits
import info.movito.themoviedbapi.model.Multi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MediaInfoViewModel @Inject constructor(
    val savedState: SavedStateHandle? = null,
    var db: FirebaseFirestore,
    var firebaseUtils: FirebaseUtils,
    var settings: Settings,
    val tvSeasonInteractor: TVSeasonInteractor,
    val tvEpisodeInteractor: TVEpisodeInteractor,
    val movieCreditsInteractor: MovieCreditsInteractor,
    val tvCreditsInteractor: TVCreditsInteractor
) : ViewModel() {
    private val mediaId = MutableStateFlow(0)
    private val mediaType = MutableStateFlow(Multi.MediaType.MOVIE)
    private val selectedMediaFlow = MutableStateFlow<SelectedMedia>(SelectedMedia.Empty)
    private val selectedSeason = MutableStateFlow(1)
    private val seasonInfo = MutableStateFlow<State<Season>>(State.loading())
    private val credits = MutableStateFlow<State<Credits>>(State.loading())
    private var watchlistSnapshotFlow = MutableStateFlow<State<QuerySnapshot>>(State.loading())

    // TODO Verify this method is feasible.
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
            loadCredits(movie.id)
        }
        tv?.let {
            setMediaType(Multi.MediaType.TV_SERIES)
            selectedMediaFlow.value = SelectedMedia.TV(tv)
            loadSeason(it.id, selectedSeason.value)
            mediaId.value = tv.id
            loadCredits(tv.id)
        }
    }

    fun getSelectedMedia(): StateFlow<SelectedMedia> = selectedMediaFlow

    val state: StateFlow<MediaInfoViewState> =
        combine(
            mediaType,
            selectedMediaFlow,
            seasonInfo,
            selectedSeason,
            credits
        ) { mediaType, selectedMedia, seasonInfo, selectedSeason, credits ->
            MediaInfoViewState(
                mediaType = mediaType,
                selectedMedia = selectedMedia,
                seasonInfo = seasonInfo,
                selectedSeason = selectedSeason,
                credits = credits
            ).apply {
                Timber.d("State: $this")
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = MediaInfoViewState.Empty
        )

    fun setMediaType(type: Multi.MediaType) {
        mediaType.value = type
    }

    private fun loadSeason(id: Int, season: Int) {
        viewModelScope.launch {
            tvSeasonInteractor.executeSync(TVSeasonInteractor.Params(id, season)).collectLatest {
                seasonInfo.value = it
            }
        }
    }

    private fun loadCredits(id: Int) {
        viewModelScope.launch {
            if (mediaType.value == Multi.MediaType.MOVIE) {
                movieCreditsInteractor.executeSync(MovieCreditsInteractor.Params(id)).collectLatest {
                    credits.value = it
                }
            } else if (mediaType.value == Multi.MediaType.TV_SERIES) {
                tvCreditsInteractor.executeSync(TVCreditsInteractor.Params(id)).collectLatest {
                    credits.value = it
                }
            }
        }
    }

    fun selectSeason(season: Int) {
        viewModelScope.launch {
            selectedSeason.value = season
        }
        loadSeason(mediaId.value, selectedSeason.value)
    }

    fun markEpisode(episodeId: Int, isWatched: Boolean) {
        if (mediaType.value == Multi.MediaType.TV_SERIES) {
            val selectedMedia = selectedMediaFlow.value as SelectedMedia.TV
            val ref = selectedMedia.docId?.let {
                db.collectionWatchdone(firebaseUtils.uid!!, settings.isUseProdDb).collectionWatchlistItems()
                    .document(it)
            }
            viewModelScope.launch {
                ref?.update("watchStatus", hashMapOf(episodeId.toString() to isWatched))?.await()
            }
        }
    }

    fun updateDocId(docId: String) {
        when (mediaType.value) {
            Multi.MediaType.MOVIE -> {
                val media = selectedMediaFlow.value as SelectedMedia.Movie
                selectedMediaFlow.value = media.copy(docId = docId)
            }
            Multi.MediaType.TV_SERIES -> {
                val media = selectedMediaFlow.value as SelectedMedia.TV
                selectedMediaFlow.value = media.copy(docId = docId)
            }
            else -> {
            }
        }
    }
}

sealed class SelectedMedia {

    data class Movie(
        val data: com.afterroot.watchdone.data.model.Movie,
        val docId: String? = null
    ) : SelectedMedia()

    data class TV(
        val data: com.afterroot.watchdone.data.model.TV,
        val docId: String? = null
    ) : SelectedMedia()

    object Empty : SelectedMedia()
}
