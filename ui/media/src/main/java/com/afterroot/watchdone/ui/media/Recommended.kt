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
package com.afterroot.watchdone.ui.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.paging.compose.LazyPagingItems
import com.afterroot.tmdbapi.repository.MoviesRepository
import com.afterroot.tmdbapi.repository.TVRepository
import com.afterroot.ui.common.compose.components.Carousel
import com.afterroot.ui.common.compose.components.PagingCarousel
import com.afterroot.watchdone.data.mapper.toMovies
import com.afterroot.watchdone.data.mapper.toTV
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.ui.common.ItemSelectedCallback

@Composable
@Deprecated("Use new paged version")
fun RecommendedMovies(
    movieId: Int,
    moviesRepository: MoviesRepository,
    movieItemSelectedCallback: ItemSelectedCallback<Movie>
) {
    val state = remember { mutableStateOf(emptyList<Movie>()) }

    LaunchedEffect(movieId) {
        state.value = moviesRepository.getRecommended(movieId).toMovies()
    }

    Carousel(
        items = state.value,
        title = "Recommended Movies",
        refreshing = state.value.isEmpty(),
        onItemClick = { movie, index ->
            movieItemSelectedCallback.onClick(index, null, movie)
        },
        onMoreClick = {}
    )
}

@Composable
@Deprecated("Use new paged version")
fun RecommendedTV(
    tvId: Int,
    tvRepository: TVRepository,
    tvItemSelectedCallback: ItemSelectedCallback<TV>,
    onMoreClick: () -> Unit
) {
    val state = remember { mutableStateOf(emptyList<TV>()) }

    LaunchedEffect(tvId) {
        state.value = tvRepository.getRecommended(tvId).toTV()
    }

    Carousel(
        items = state.value,
        title = "Recommended TV Series",
        refreshing = state.value.isEmpty(),
        onItemClick = { tv, index ->
            tvItemSelectedCallback.onClick(index, null, tv)
        },
        onMoreClick = onMoreClick
    )
}

@Composable
fun RecommendedMoviesPaged(
    items: LazyPagingItems<Movie>,
    onItemClick: (Movie, Int) -> Unit
) {
    PagingCarousel(
        items = items,
        title = "Recommended Movies",
        refreshing = false,
        onItemClick = onItemClick,
        onMoreClick = null
    )
}

@Composable
fun RecommendedTVPaged(
    items: LazyPagingItems<TV>,
    onItemClick: (TV, Int) -> Unit
) {
    PagingCarousel(
        items = items,
        title = "Recommended TV Series",
        refreshing = false,
        onItemClick = onItemClick,
        onMoreClick = null
    )
}
