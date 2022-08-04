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
package com.afterroot.watchdone.watchlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.afterroot.tmdbapi.model.Multi
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.resources.R as CommonR

@Composable
fun Watchlist(
    viewModel: WatchlistViewModel = viewModel(),
    navController: NavController
) {
    val state = viewModel.getWatchlistItems().collectAsState(initial = emptyList())

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(state.value) {
            when (it.mediaType) {
                Multi.MediaType.MOVIE -> {
                    val movie = it as Movie
                    WatchlistItem(
                        poster = viewModel.settings.createPosterUrl(movie.posterPath.toString()),
                        title = movie.title
                    )
                }
                Multi.MediaType.TV_SERIES -> {
                    val tv = it as TV
                    WatchlistItem(
                        poster = viewModel.settings.createPosterUrl(tv.posterPath.toString()),
                        title = tv.name
                    )
                }
                else -> {
                    // SKIP
                }
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun WatchlistItem(poster: String?, title: String?) {
    Column(Modifier.fillMaxWidth()) {
        Image(
            painter = rememberImagePainter(
                data = poster,
                builder = {
                    crossfade(true)
                    placeholder(CommonR.drawable.ic_placeholder_movie)
                }
            ),
            contentDescription = title,
            modifier = Modifier.aspectRatio(3f / 2f)
        )
        Row(Modifier.fillMaxHeight()) {
            if (title != null) {
                Text(text = title)
            }
        }
    }
}
