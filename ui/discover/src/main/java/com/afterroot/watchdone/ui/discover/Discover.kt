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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import app.tivi.common.compose.fullSpanItem
import app.tivi.common.compose.gridItems
import app.tivi.common.compose.ui.plus
import com.afterroot.ui.common.compose.components.CommonAppBar
import com.afterroot.ui.common.compose.components.FilterChipGroup
import com.afterroot.ui.common.compose.components.MovieCard
import com.afterroot.ui.common.compose.components.TVCard
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.ui.common.ItemSelectedCallback
import com.afterroot.watchdone.viewmodel.DiscoverActions
import com.afterroot.watchdone.viewmodel.DiscoverViewModel
import info.movito.themoviedbapi.model.Multi

@Composable
fun DiscoverChips(
    onMovieSelected: () -> Unit,
    onTVSelected: () -> Unit
) {
    FilterChipGroup(
        modifier = Modifier.padding(vertical = 8.dp),
        chipSpacing = 12.dp,
        horizontalPadding = 8.dp,
        icons = listOf(Icons.Outlined.Movie, Icons.Outlined.Tv),
        list = listOf("Movies", "TV"),
        preSelect = listOf("Movies")
    ) { selected, _ ->
        when (selected) {
            "Movies" -> onMovieSelected()
            "TV" -> onTVSelected()
        }
    }
}

@Composable
fun Discover(
    discoverViewModel: DiscoverViewModel = hiltViewModel(),
    itemSelectedCallback: ItemSelectedCallback<Multi>
) {
    val viewState by discoverViewModel.state.collectAsState()
    val movieItems = discoverViewModel.discoverMovies.collectAsLazyPagingItems()
    val tvItems = discoverViewModel.discoverTV.collectAsLazyPagingItems()

    Discover(
        state = viewState.copy(isLoading = movieItems.loadState.refresh is LoadState.Loading || tvItems.loadState.refresh is LoadState.Loading),
        movieItems = movieItems,
        tvItems = tvItems,
        itemSelectedCallback = itemSelectedCallback,
        onMovieChipSelected = {
            discoverViewModel.submitAction(DiscoverActions.SetMediaType(Multi.MediaType.MOVIE))
        },
        onTVChipSelected = {
            discoverViewModel.submitAction(DiscoverActions.SetMediaType(Multi.MediaType.TV_SERIES))
        }
    ) {
        if (viewState.mediaType == Multi.MediaType.MOVIE) {
            movieItems.refresh()
        } else if (viewState.mediaType == Multi.MediaType.TV_SERIES) {
            tvItems.refresh()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
internal fun Discover(
    state: DiscoverViewState,
    movieItems: LazyPagingItems<Movie>,
    tvItems: LazyPagingItems<TV>,
    itemSelectedCallback: ItemSelectedCallback<Multi>,
    onMovieChipSelected: () -> Unit,
    onTVChipSelected: () -> Unit,
    refresh: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            CommonAppBar(withTitle = "Discover", scrollBehavior = scrollBehavior)
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        val refreshState = rememberPullRefreshState(
            refreshing = state.isLoading,
            onRefresh = refresh
        )

        Box(modifier = Modifier.pullRefresh(state = refreshState)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = paddingValues + PaddingValues(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .fillMaxHeight()
            ) {
                fullSpanItem {
                    DiscoverChips(onMovieSelected = { onMovieChipSelected() }, onTVSelected = { onTVChipSelected() })
                }
                if (state.mediaType == Multi.MediaType.MOVIE) {
                    gridItems(items = movieItems, key = { it.id }) { movie ->
                        if (movie != null) {
                            MovieCard(
                                movie = movie,
                                onClick = {
                                    itemSelectedCallback.onClick(0, null, movie)
                                },
                                modifier = Modifier
                                    .animateItemPlacement()
                                    .fillMaxWidth()
                                    .aspectRatio(2 / 3f)
                            )
                        }
                    }
                } else if (state.mediaType == Multi.MediaType.TV_SERIES) {
                    gridItems(items = tvItems, key = { it.id }) { tv ->
                        if (tv != null) {
                            TVCard(
                                tv = tv,
                                onClick = {
                                    itemSelectedCallback.onClick(0, null, tv)
                                },
                                modifier = Modifier
                                    .animateItemPlacement()
                                    .fillMaxWidth()
                                    .aspectRatio(2 / 3f)
                            )
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = state.isLoading,
                state = refreshState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(paddingValues),
                scale = true
            )
        }
    }
}
