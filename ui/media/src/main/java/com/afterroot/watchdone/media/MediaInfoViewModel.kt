/*
 * Copyright (C) 2020-2021 Sandip Vaghela
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
package com.afterroot.watchdone.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.afterroot.watchdone.base.Collection
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.utils.collectionWatchdone
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MediaInfoViewModel(val savedState: SavedStateHandle? = null) : ViewModel(), KoinComponent {

    private val db: FirebaseFirestore by inject()
    private val settings: Settings by inject()
    private var watchlistSnapshot = MutableLiveData<State>()
    private val selectedMedia = MutableLiveData<SelectedMedia>()

    companion object {
        const val MOVIE = "movie"
        const val TV = "tv"
        const val MEDIA = "media"
    }

    fun getWatchlistSnapshot(
        userId: String,
        isReload: Boolean = false,
        additionQueries: (Query.() -> Query)? = null
    ): LiveData<State> {
        watchlistSnapshot.apply {
            if (value == null || isReload) {
                value = State.Loading
                val ref = db.collectionWatchdone(id = userId, settings.isUseProdDb)
                    .document(Collection.WATCHLIST)
                    .collection(Collection.ITEMS)
                if (additionQueries == null) {
                    ref.addSnapshotListener { querySnapshot, _ ->
                        querySnapshot.let { value = State.Loaded(it) }
                    }
                } else {
                    ref.additionQueries().addSnapshotListener { querySnapshot, _ ->
                        querySnapshot.let { value = State.Loaded(it) }
                    }
                }
            }
        }
        return watchlistSnapshot
    }

    fun selectMedia(movie: Movie? = null, tv: TV? = null) {
        movie?.let { selectedMedia.value = SelectedMedia.Movie(movie) }
        tv?.let { selectedMedia.value = SelectedMedia.TV(tv) }
    }

    fun getSelectedMedia(): LiveData<SelectedMedia> = selectedMedia
}

sealed class State {
    object Loading : State()
    data class Loaded<T>(val data: T) : State()
    data class Error<T>(val error: T) : State()
}

sealed class SelectedMedia {
    data class Movie(val data: com.afterroot.watchdone.data.model.Movie) : SelectedMedia()
    data class TV(val data: com.afterroot.watchdone.data.model.TV) : SelectedMedia()
}
