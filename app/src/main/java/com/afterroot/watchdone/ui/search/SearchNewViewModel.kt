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
package com.afterroot.watchdone.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afterroot.tmdbapi2.repository.SearchRepository
import com.afterroot.watchdone.viewmodel.ViewModelState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchNewViewModel @Inject constructor(val repository: SearchRepository) : ViewModel() {
    private var movies = MutableLiveData<ViewModelState>()
    private var tv = MutableLiveData<ViewModelState>()
    private var people = MutableLiveData<ViewModelState>()
    private var previousQuery: String? = null
    fun searchMovies(query: String, includeAdult: Boolean = true): LiveData<ViewModelState> {
        if (previousQuery != query) {
            movies.value = ViewModelState.Loading
            viewModelScope.launch(Dispatchers.IO) {
                val result = ViewModelState.Loaded(repository.searchMovie(query, includeAdult))
                withContext(Dispatchers.Main) {
                    movies.value = result
                    previousQuery = query
                }
            }
        }
        return movies
    }

    fun searchTV(query: String, includeAdult: Boolean = true): LiveData<ViewModelState> {
        if (previousQuery != query) {
            tv.value = ViewModelState.Loading
            viewModelScope.launch(Dispatchers.IO) {
                val result = ViewModelState.Loaded(repository.searchTv(query, includeAdult))
                withContext(Dispatchers.Main) {
                    tv.value = result
                    previousQuery = query
                }
            }
        }
        return tv
    }

    fun searchPeople(query: String, includeAdult: Boolean = true): LiveData<ViewModelState> {
        if (previousQuery != query) {
            people.value = ViewModelState.Loading
            viewModelScope.launch(Dispatchers.IO) {
                val result = ViewModelState.Loaded(repository.searchPerson(query, includeAdult))
                withContext(Dispatchers.Main) {
                    people.value = result
                    previousQuery = query
                }
            }
        }
        return people
    }
}
