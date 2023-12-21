/*
 * Copyright (C) 2020-2023 Sandip Vaghela
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import app.tivi.common.compose.fullSpanItem
import app.tivi.common.compose.ui.plus
import com.afterroot.ui.common.compose.components.CommonAppBar
import com.afterroot.ui.common.compose.components.FilterChipGroup
import com.afterroot.ui.common.compose.components.MediaCard
import com.afterroot.ui.common.compose.utils.TopBarWindowInsets
import com.afterroot.watchdone.data.compoundmodel.DiscoverEntryWithMedia
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.ui.common.ItemSelectedCallback
import com.afterroot.watchdone.viewmodel.DiscoverViewModel
import com.afterroot.watchdone.resources.R as CommonR

@Composable
fun DiscoverChips(
    onMovieSelected: () -> Unit,
    onShowSelected: () -> Unit,
) {
    val movieText = stringResource(id = CommonR.string.text_search_movies)
    val showText = stringResource(id = CommonR.string.text_search_show)
    FilterChipGroup(
        modifier = Modifier.padding(vertical = 8.dp),
        chipSpacing = 12.dp,
        horizontalPadding = 8.dp,
        icons = listOf(Icons.Outlined.Movie, Icons.Outlined.Tv),
        list = listOf(movieText, showText),
        preSelect = listOf(movieText),
        onSelectedChanged = { selected, _ ->
            when (selected) {
                movieText -> onMovieSelected()
                showText -> onShowSelected()
            }
        },
    )
}

@Composable
fun Discover(
    discoverViewModel: DiscoverViewModel = hiltViewModel(),
    itemSelectedCallback: ItemSelectedCallback<Media>,
) {
    val viewState by discoverViewModel.state.collectAsState()
    val discoverItems = discoverViewModel.pagedDiscoverList.collectAsLazyPagingItems()

    Discover(
        state = viewState.copy(
            isLoading = discoverItems.loadState.refresh is LoadState.Loading,
        ),
        movieItems = discoverItems,
        showItems = discoverItems,
        itemSelectedCallback = itemSelectedCallback,
        onMovieChipSelected = {
            discoverViewModel.submitAction(DiscoverActions.SetMediaType(MediaType.MOVIE))
        },
        onShowChipSelected = {
            discoverViewModel.submitAction(DiscoverActions.SetMediaType(MediaType.SHOW))
        },
        refresh = discoverItems::refresh,
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class,
)
@Composable
internal fun Discover(
    state: DiscoverViewState,
    movieItems: LazyPagingItems<DiscoverEntryWithMedia>,
    showItems: LazyPagingItems<DiscoverEntryWithMedia>,
    itemSelectedCallback: ItemSelectedCallback<Media>,
    onMovieChipSelected: () -> Unit,
    onShowChipSelected: () -> Unit,
    refresh: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val listState = rememberLazyGridState()

    Scaffold(
        topBar = {
            CommonAppBar(
                withTitle = "Discover",
                scrollBehavior = scrollBehavior,
                windowInsets = TopBarWindowInsets,
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        val refreshState = rememberPullRefreshState(
            refreshing = state.isLoading,
            onRefresh = refresh,
        )

        Box(
            modifier = Modifier
                .pullRefresh(state = refreshState)
                .fillMaxWidth(),
        ) {
            if ((state.mediaType == MediaType.MOVIE && movieItems.itemCount != 0 || state.mediaType == MediaType.SHOW && showItems.itemCount != 0) || !state.isLoading) {
                LazyVerticalGrid(
                    state = listState,
                    columns = GridCells.Fixed(3),
                    contentPadding = paddingValues + PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .fillMaxHeight(),
                ) {
                    fullSpanItem {
                        DiscoverChips(
                            onMovieSelected = { onMovieChipSelected() },
                            onShowSelected = { onShowChipSelected() },
                        )
                    }
                    if (state.mediaType == MediaType.MOVIE) {
                        items(
                            count = movieItems.itemCount,
                            key = movieItems.itemKey { it.media.id },
                        ) { index ->
                            val movie = movieItems[index]
                            if (movie != null) {
                                MediaCard(
                                    media = movie.media,
                                    onClick = {
                                        itemSelectedCallback.onClick(0, null, movie.media)
                                    },
                                    modifier = Modifier
                                        .animateItemPlacement()
                                        .fillMaxWidth()
                                        .aspectRatio(2 / 3f),
                                )
                            }
                        }
                    } else if (state.mediaType == MediaType.SHOW) {
                        items(
                            count = showItems.itemCount,
                            key = showItems.itemKey { it.media.id },
                        ) { index ->
                            val show = showItems[index]
                            if (show != null) {
                                MediaCard(
                                    media = show.media,
                                    onClick = {
                                        itemSelectedCallback.onClick(0, null, show.media)
                                    },
                                    modifier = Modifier
                                        .animateItemPlacement()
                                        .fillMaxWidth()
                                        .aspectRatio(2 / 3f),
                                )
                            }
                        }
                    }
                    fullSpanItem {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = state.isLoading,
                state = refreshState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(paddingValues),
                scale = true,
            )
        }
    }
}
