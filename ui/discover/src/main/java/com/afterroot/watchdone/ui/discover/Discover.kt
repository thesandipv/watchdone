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
package com.afterroot.watchdone.ui.discover

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.afterroot.ui.common.compose.components.FilterChipGroup
import com.afterroot.ui.common.compose.components.MovieCard
import com.afterroot.ui.common.compose.components.TVCard
import com.afterroot.ui.common.compose.components.gridItems
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.ui.common.ItemSelectedCallback
import com.afterroot.watchdone.viewmodel.DiscoverActions
import com.afterroot.watchdone.viewmodel.DiscoverViewModel
import info.movito.themoviedbapi.model.Multi
import com.afterroot.watchdone.resources.R as CommonR

@Composable
fun DiscoverChips(discoverViewModel: DiscoverViewModel = hiltViewModel()) {
    FilterChipGroup(
        modifier = Modifier.padding(vertical = 8.dp),
        chipSpacing = 12.dp,
        horizontalPadding = dimensionResource(id = CommonR.dimen.padding_horizontal),
        icons = listOf(Icons.Outlined.Movie, Icons.Outlined.Tv),
        list = listOf("Movies", "TV"),
        preSelect = listOf("Movies")
    ) { selected, _ ->
        when (selected) {
            "Movies" -> {
                discoverViewModel.submitAction(DiscoverActions.SetMediaType(Multi.MediaType.MOVIE))
            }
            "TV" -> {
                discoverViewModel.submitAction(DiscoverActions.SetMediaType(Multi.MediaType.TV_SERIES))
            }
        }
    }
}

@Composable
fun Discover(
    discoverViewModel: DiscoverViewModel = hiltViewModel(),
    itemSelectedCallback: ItemSelectedCallback<Multi>
) {
    val viewState by discoverViewModel.state.collectAsState()
    Column {
        DiscoverChips()
        when (viewState.mediaType) {
            Multi.MediaType.MOVIE -> MovieDiscover(
                movieItems = discoverViewModel.discoverMovies.collectAsLazyPagingItems(),
                onClick = {
                    itemSelectedCallback.onClick(0, null, it)
                }
            )
            Multi.MediaType.TV_SERIES -> TVDiscover(
                tvItems = discoverViewModel.discoverTV.collectAsLazyPagingItems(),
                onClick = {
                    itemSelectedCallback.onClick(0, null, it)
                }
            )
            else -> {
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovieDiscover(movieItems: LazyPagingItems<Movie>, onClick: (Movie) -> Unit = {}) {
    // TODO Extract standard movie grid from compose
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        gridItems(items = movieItems, key = { it.id }) { movie ->
            if (movie != null) {
                MovieCard(
                    movie = movie,
                    onClick = { onClick(movie) },
                    modifier = Modifier
                        .animateItemPlacement()
                        .fillMaxWidth()
                        .aspectRatio(2 / 3f)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TVDiscover(tvItems: LazyPagingItems<TV>, onClick: (TV) -> Unit = {}) {
    // TODO Extract standard tv grid from compose
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        gridItems(items = tvItems, key = { it.id }) { tv ->
            if (tv != null) {
                TVCard(
                    tv = tv,
                    onClick = { onClick(tv) },
                    modifier = Modifier
                        .animateItemPlacement()
                        .fillMaxWidth()
                        .aspectRatio(2 / 3f)
                )
            }
        }
    }
}
