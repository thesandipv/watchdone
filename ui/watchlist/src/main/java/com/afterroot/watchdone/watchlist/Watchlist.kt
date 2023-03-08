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
package com.afterroot.watchdone.watchlist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import app.tivi.common.compose.fullSpanItem
import app.tivi.common.compose.gridItemsIndexed
import app.tivi.common.compose.ui.plus
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.afterroot.ui.common.compose.components.CommonAppBar
import com.afterroot.ui.common.compose.components.LocalPosterSize
import com.afterroot.ui.common.compose.components.LocalTMDbBaseUrl
import com.afterroot.ui.common.compose.theme.ubuntuTypography
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.ui.common.ItemSelectedCallback
import com.afterroot.watchdone.ui.media.MetaText
import info.movito.themoviedbapi.model.Multi

@Composable
fun Watchlist(
    viewModel: WatchlistViewModel = viewModel(),
    itemSelectedCallback: ItemSelectedCallback<Multi>
) {
    val state by viewModel.state.collectAsState()
    val watchlist = viewModel.watchlist.collectAsLazyPagingItems()

    Watchlist(
        state = state.copy(loading = watchlist.loadState.refresh == LoadState.Loading),
        watchlist = watchlist,
        itemSelectedCallback = itemSelectedCallback,
        refresh = {
            watchlist.refresh()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
private fun Watchlist(
    state: WatchlistState,
    watchlist: LazyPagingItems<Multi>,
    itemSelectedCallback: ItemSelectedCallback<Multi>,
    refresh: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            CommonAppBar(withTitle = "Watchlist", scrollBehavior = scrollBehavior)
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        val refreshState = rememberPullRefreshState(
            refreshing = state.loading,
            onRefresh = refresh
        )

        Box(modifier = Modifier.pullRefresh(state = refreshState)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = paddingValues + PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .fillMaxHeight()
            ) {
                fullSpanItem {
                    // TODO Filters here
                }
                gridItemsIndexed(items = watchlist, key = { index, item ->
                    when (item.mediaType) {
                        Multi.MediaType.MOVIE -> {
                            (item as Movie).id
                        }

                        Multi.MediaType.TV_SERIES -> {
                            (item as TV).id
                        }

                        else -> {
                            index
                        }
                    }
                }) { _, item ->
                    when (item?.mediaType) {
                        Multi.MediaType.MOVIE -> {
                            item as Movie
                            WatchlistItem(
                                poster = item.posterPath,
                                title = item.title,
                                rating = item.rating(),
                                releaseDate = item.releaseDate,
                                modifier = Modifier
                                    .animateItemPlacement()
                                    .fillMaxWidth()
                                    .aspectRatio(2 / 3f),
                                isWatched = item.isWatched,
                                mediaType = item.mediaType,
                                onClick = {
                                    itemSelectedCallback.onClick(0, null, item)
                                }
                            )
                        }

                        Multi.MediaType.TV_SERIES -> {
                            item as TV
                            WatchlistItem(
                                poster = item.posterPath,
                                title = item.name,
                                rating = item.rating(),
                                releaseDate = item.releaseDate,
                                modifier = Modifier
                                    .animateItemPlacement()
                                    .fillMaxWidth()
                                    .aspectRatio(2 / 3f),
                                isWatched = item.isWatched,
                                mediaType = item.mediaType,
                                onClick = {
                                    itemSelectedCallback.onClick(0, null, item)
                                }
                            )
                        }

                        else -> {
                            // BLANK
                        }
                    }
                }
                fullSpanItem {
                    Spacer(modifier = Modifier.height(80.dp)) // Adjustment
                }
            }

            PullRefreshIndicator(
                refreshing = state.loading,
                state = refreshState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(paddingValues),
                scale = true
            )
        }
    }
}

@Composable
fun WatchlistItem(
    poster: String?,
    title: String?,
    rating: String?,
    releaseDate: String?,
    mediaType: Multi.MediaType,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    isWatched: Boolean = false,
    onClick: (() -> Unit)? = {}
) {
    Surface(
        color = MaterialTheme.colorScheme.onSurface
            .copy(alpha = 0.2f)
            .compositeOver(MaterialTheme.colorScheme.surface),
        shape = shape,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
        ) {
            poster?.let {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(LocalTMDbBaseUrl.current + LocalPosterSize.current + poster).crossfade(true).build(),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
            }
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0xB3000000),
                                Color.Transparent,
                                Color(0xB3000000)
                            )
                        )
                    )
                    .padding(all = 8.dp)
                    .matchParentSize()
            ) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    if (mediaType == Multi.MediaType.MOVIE) {
                        Icon(
                            imageVector = Icons.Rounded.Movie,
                            contentDescription = "Movie",
                            modifier = Modifier.align(Alignment.CenterVertically),
                            tint = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.LiveTv,
                            contentDescription = "TV Series",
                            modifier = Modifier.align(Alignment.CenterVertically),
                            tint = Color.White
                        )
                    }
                    if (isWatched) {
                        Icon(
                            imageVector = Icons.Rounded.Done,
                            contentDescription = "Watched",
                            modifier = Modifier.align(Alignment.CenterVertically),
                            tint = Color.White
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                ) {
                    title?.let {
                        ProvideTextStyle(value = ubuntuTypography.bodySmall) {
                            Text(text = it, softWrap = false, overflow = TextOverflow.Ellipsis)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        releaseDate?.let {
                            ProvideTextStyle(value = ubuntuTypography.bodySmall) {
                                Text(text = it, modifier = Modifier.align(Alignment.Bottom))
                            }
                        }
                        rating?.let {
                            MetaText(
                                text = it,
                                modifier = Modifier.align(Alignment.Bottom),
                                icon = Icons.Rounded.Star
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewGradient() {
    WatchlistItem(
        poster = null,
        title = "test",
        rating = "7.5",
        releaseDate = "2022-05-20",
        mediaType = Multi.MediaType.MOVIE
    )
}
