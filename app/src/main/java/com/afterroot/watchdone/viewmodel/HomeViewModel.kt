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

package com.afterroot.watchdone.viewmodel

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.afterroot.tmdbapi.model.MovieDb
import com.afterroot.tmdbapi.model.core.MovieResultsPage
import com.afterroot.tmdbapi2.model.RequestBodyToken
import com.afterroot.tmdbapi2.repository.AuthRepository
import com.afterroot.tmdbapi2.repository.GenresRepository
import com.afterroot.tmdbapi2.repository.MoviesRepository
import com.afterroot.watchdone.data.model.Collection
import com.afterroot.watchdone.database.MyDatabase
import com.afterroot.watchdone.utils.Event
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import com.google.firebase.firestore.Query as FirestoreQuery

class HomeViewModel(val savedState: SavedStateHandle? = null) : ViewModel(), KoinComponent {
    private val db: FirebaseFirestore by inject()
    private var trendingMovies = MutableLiveData<MovieResultsPage>()
    private var watchlistSnapshot = MutableLiveData<ViewModelState>()
    val error = MutableLiveData<Event<String>>()
    val selectedMovie = MutableLiveData<MovieDb>()

    fun getWatchlistSnapshot(
        userId: String,
        isReload: Boolean = false,
        additionQueries: (FirestoreQuery.() -> FirestoreQuery)? = null
    ): LiveData<ViewModelState> {
        watchlistSnapshot.apply {
            if (value == null || isReload) {
                value = ViewModelState.Loading
                val ref = db.collection(Collection.USERS).document(userId)
                    .collection(Collection.WATCHDONE)
                    .document(Collection.WATCHLIST)
                    .collection(Collection.ITEMS)
                if (additionQueries == null) {
                    ref.addSnapshotListener { querySnapshot, _ ->
                        querySnapshot.let { value = ViewModelState.Loaded(it) }
                    }
                } else {
                    ref.additionQueries().addSnapshotListener { querySnapshot, _ ->
                        querySnapshot.let { value = ViewModelState.Loaded(it) }
                    }
                }
            }
        }
        return watchlistSnapshot
    }

    fun getTrendingMovies(isReload: Boolean = false): LiveData<MovieResultsPage> {
        if (trendingMovies.value == null || isReload) {
            try {
                trendingMovies = liveData(Dispatchers.IO) { //Background Thread
                    emit(get<MoviesRepository>().getMoviesTrendingInSearch())
                } as MutableLiveData<MovieResultsPage>
            } catch (e: Exception) {
                error.value = Event(e.message!!)
                Log.e("TMDbApi", "getTrendingMovies: ${e.message}")
            }
        }
        return trendingMovies
    }

    /**
     * For sending data to other fragment
     */
    fun selectMovie(movie: MovieDb) {
        selectedMovie.value = movie
    }

    fun getResponseRequestToken() = liveData(Dispatchers.IO) {
        emit(
            get<AuthRepository>().createRequestToken(RequestBodyToken("https://afterroot.web.app/apps/watchdone/launch"))
        )
    }

    fun addGenres(owner: LifecycleOwner) {
        get<MyDatabase>().genreDao().apply {
            getGenres().observe(owner, Observer {
                if (it.isNullOrEmpty()) {
                    viewModelScope.launch {
                        this@apply.add(get<GenresRepository>().getMoviesGenres().genres)
                    }
                }
            })
        }
    }
}