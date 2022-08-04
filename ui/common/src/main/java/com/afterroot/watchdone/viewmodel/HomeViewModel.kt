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

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.afterroot.tmdbapi.model.RequestBodyToken
import com.afterroot.tmdbapi.repository.AuthRepository
import com.afterroot.tmdbapi.repository.GenresRepository
import com.afterroot.tmdbapi.repository.MoviesRepository
import com.afterroot.watchdone.base.Collection
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.database.MyDatabase
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.utils.collectionWatchdone
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import info.movito.themoviedbapi.model.core.MovieResultsPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import com.google.firebase.firestore.Query as FirestoreQuery

@HiltViewModel
class HomeViewModel @Inject constructor(
    val savedState: SavedStateHandle? = null,
    private val db: FirebaseFirestore,
    private val settings: Settings,
    private val myDatabase: MyDatabase,
    private val authRepository: AuthRepository,
    private val genresRepository: GenresRepository,
    private val moviesRepository: MoviesRepository
) : ViewModel() {
    private var trendingMovies = MutableLiveData<MovieResultsPage>()
    private var watchlistSnapshot = MutableLiveData<ViewModelState>()
    val error = MutableLiveData<Event<String>>()
    val selectedMovie = MutableLiveData<Movie>()
    val selectedTvSeries = MutableLiveData<TV>()

    fun getWatchlistSnapshot(
        userId: String,
        isReload: Boolean = false,
        additionQueries: (FirestoreQuery.() -> FirestoreQuery)? = null
    ): LiveData<ViewModelState> {
        watchlistSnapshot.apply {
            if (value == null || isReload) {
                value = ViewModelState.Loading
                val ref = db.collectionWatchdone(id = userId, settings.isUseProdDb)
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
                trendingMovies = liveData(Dispatchers.IO) { // Background Thread
                    emit(moviesRepository.getMoviesTrendingInSearch())
                } as MutableLiveData<MovieResultsPage>
            } catch (e: Exception) {
                error.value = Event(e.message!!)
                Timber.e(e, "getTrendingMovies: ${e.message}")
            }
        }
        return trendingMovies
    }

    /**
     * For sending data to other fragment
     */
    fun selectMovie(movie: Movie) {
        selectedMovie.value = movie
    }

    fun selectTVSeries(tvSeries: TV) {
        selectedTvSeries.value = tvSeries
    }

    fun getResponseRequestToken() = liveData(Dispatchers.IO) {
        emit( // TODO Deeplink properly
            authRepository.createRequestToken(RequestBodyToken("https://afterroot.web.app/apps/watchdone/launch"))
        )
    }

    fun addGenres(owner: LifecycleOwner) {
        myDatabase.genreDao().apply {
            getGenres().observe(owner) {
                if (it.isNullOrEmpty()) {
                    viewModelScope.launch {
                        this@apply.add(genresRepository.getMoviesGenres().genres)
                    }
                }
            }
        }
    }
}
