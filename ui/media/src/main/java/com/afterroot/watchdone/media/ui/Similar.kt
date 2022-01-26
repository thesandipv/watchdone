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
package com.afterroot.watchdone.media.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleCoroutineScope
import com.afterroot.tmdbapi2.repository.MoviesRepository
import com.afterroot.tmdbapi2.repository.TVRepository
import com.afterroot.watchdone.data.mapper.toMovies
import com.afterroot.watchdone.data.mapper.toTV
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.media.adapter.SearchMoviesListAdapter
import com.afterroot.watchdone.media.adapter.SearchTVListAdapter
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.common.ItemSelectedCallback
import com.afterroot.watchdone.ui.view.SectionalListView
import kotlinx.coroutines.launch

@Composable
fun SimilarMovies(
    movieId: Int,
    settings: Settings,
    lifecycleCoroutineScope: LifecycleCoroutineScope,
    moviesRepository: MoviesRepository,
    movieItemSelectedCallback: ItemSelectedCallback<Movie>
) {
    val state = remember { mutableStateOf(emptyList<Movie>()) }
    val moviesListAdapter = SearchMoviesListAdapter(movieItemSelectedCallback, settings)

    AndroidView(
        factory = {
            SectionalListView(it).withTitle("Similar Movies").withLoading().apply {
                setAdapter(moviesListAdapter)
                lifecycleCoroutineScope.launch {
                    state.value = moviesRepository.getSimilar(movieId).toMovies()
                }
            }
        },
        modifier = Modifier
            .fillMaxSize(),
        // .padding(horizontal = 8.dp)
        // .clipToBounds(),
        update = {
            it.isLoaded = true
            (it.list.adapter as SearchMoviesListAdapter).submitList(state.value)
            moviesListAdapter.submitList(state.value)
        }
    )
}

@Composable
fun SimilarTV(
    tvId: Int,
    settings: Settings,
    lifecycleCoroutineScope: LifecycleCoroutineScope,
    tvRepository: TVRepository,
    tvItemSelectedCallback: ItemSelectedCallback<TV>
) {
    val state = remember { mutableStateOf(emptyList<TV>()) }
    val tvListAdapter = SearchTVListAdapter(tvItemSelectedCallback, settings)

    AndroidView(
        factory = {
            SectionalListView(it).withTitle("Similar Shows").withLoading().apply {
                setAdapter(tvListAdapter)
                lifecycleCoroutineScope.launch {
                    state.value = tvRepository.getSimilar(tvId).toTV()
                }
            }
        },
        modifier = Modifier
            .fillMaxSize(),
        // .padding(horizontal = 8.dp)
        // .clipToBounds(),
        update = {
            it.isLoaded = true
            (it.list.adapter as SearchTVListAdapter).submitList(state.value)
        }
    )
}
