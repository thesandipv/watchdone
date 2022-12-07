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
package com.afterroot.watchdone.recommended.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.afterroot.ui.common.compose.components.PagingCarousel
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.recommended.viewmodel.RecommendedViewModel
import com.afterroot.watchdone.ui.common.ItemSelectedCallback

@Composable
fun RecommendedMoviesPaged(
    movieId: Int,
    recommendedViewModel: RecommendedViewModel = hiltViewModel(),
    movieItemSelectedCallback: ItemSelectedCallback<Movie>
) {
    val pagingItems = recommendedViewModel.getRecommendedMovies(movieId).collectAsLazyPagingItems()
    PagingCarousel(
        items = pagingItems,
        title = "Recommended Movies",
        refreshing = false,
        onItemClick = { movie, index ->
            movieItemSelectedCallback.onClick(index, null, movie)
        },
        onMoreClick = null
    )
}

@Composable
fun RecommendedTVPaged(
    tvId: Int,
    recommendedViewModel: RecommendedViewModel = hiltViewModel(),
    tvItemSelectedCallback: ItemSelectedCallback<TV>
) {
    val pagingItems = recommendedViewModel.getRecommendedShows(tvId).collectAsLazyPagingItems()
    PagingCarousel(
        items = pagingItems,
        title = "Recommended TV Series",
        refreshing = false,
        onItemClick = { tv, index ->
            tvItemSelectedCallback.onClick(index, null, tv)
        },
        onMoreClick = null
    )
}
