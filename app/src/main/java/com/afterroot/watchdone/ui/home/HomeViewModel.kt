/*
 * Copyright (C) 2020 Sandip Vaghela
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

package com.afterroot.watchdone.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afterroot.tmdbapi.model.MovieDb
import com.afterroot.watchdone.database.DatabaseFields
import com.google.firebase.firestore.FirebaseFirestore

class HomeViewModel : ViewModel() {
    private var watchlistSnapshot: MutableLiveData<ViewModelState> = MutableLiveData()
    val selected = MutableLiveData<MovieDb>()

    fun getWatchlistSnapshot(userId: String, db: FirebaseFirestore): LiveData<ViewModelState> {
        if (watchlistSnapshot.value == null) {
            watchlistSnapshot.postValue(ViewModelState.Loading)
            db.collection(DatabaseFields.COLLECTION_USERS).document(userId).collection(DatabaseFields.COLLECTION_WATCHDONE)
                .addSnapshotListener { querySnapshot, _ ->
                    if (querySnapshot != null) {
                        watchlistSnapshot.postValue(ViewModelState.Loaded(querySnapshot))
                    }
                }
        }
        return watchlistSnapshot
    }

    /**
     * For sending data to other fragment
     */
    fun selectMovie(movie: MovieDb) {
        selected.value = movie
    }
}

sealed class ViewModelState {
    object Loading : ViewModelState()
    data class Loaded<T>(val data: T) : ViewModelState()
}