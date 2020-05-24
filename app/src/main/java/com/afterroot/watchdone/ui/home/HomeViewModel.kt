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

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.afterroot.tmdbapi.model.MovieDb
import com.afterroot.tmdbapi.model.core.MovieResultsPage
import com.afterroot.tmdbapi2.model.RequestBodyToken
import com.afterroot.tmdbapi2.repository.AuthRepository
import com.afterroot.tmdbapi2.repository.MoviesRepository
import com.afterroot.watchdone.BuildConfig
import com.afterroot.watchdone.database.DatabaseFields
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import org.koin.core.KoinComponent
import org.koin.core.get

class HomeViewModel(val savedState: SavedStateHandle) : ViewModel(), KoinComponent {
    private var watchlistSnapshot: MutableLiveData<ViewModelState> = MutableLiveData()
    private var trendingMovies: MutableLiveData<MovieResultsPage> = MutableLiveData()
    val error: MutableLiveData<String?> = MutableLiveData()
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

    fun getTrendingMovies(isReload: Boolean = false): LiveData<MovieResultsPage> {
        if (trendingMovies.value == null || isReload) {
            trendingMovies = liveData(Dispatchers.IO) {
                try {
                    emit(get<MoviesRepository>().getMoviesTrendingInSearch())
                } catch (e: Exception) {
                    error.postValue(e.message)
                    Log.e("TMDbApi", "getTrendingMovies: ${e.message}")
                }
            } as MutableLiveData<MovieResultsPage>
        }
        return trendingMovies
    }

    /**
     * For sending data to other fragment
     */
    fun selectMovie(movie: MovieDb) {
        selected.value = movie
    }

    fun getResponseRequestToken() = liveData(Dispatchers.IO) {
        emit(
            get<AuthRepository>().createRequestToken(
                BuildConfig.TMDB_BEARER_TOKEN,
                RequestBodyToken("https://afterroot.web.app/apps/watchdone")
            )
        )
    }
}

sealed class ViewModelState {
    object Loading : ViewModelState()
    data class Loaded<T>(val data: T) : ViewModelState()
}