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

package com.afterroot.watchdone.ui.media

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import app.tivi.common.compose.Layout
import com.afterroot.ui.common.compose.components.Backdrop
import com.afterroot.ui.common.compose.components.CommonAppBar
import com.afterroot.ui.common.compose.components.PagingCarousel
import com.afterroot.ui.common.compose.theme.ubuntuTypography
import com.afterroot.watchdone.data.model.DBMedia
import com.afterroot.watchdone.data.model.Episode
import com.afterroot.watchdone.viewmodel.MediaInfoViewModel
import info.movito.themoviedbapi.model.Multi

@Composable
fun MediaInfo(navigateUp: () -> Unit, onRecommendedClick: (media: Multi) -> Unit) {
    MediaInfo(viewModel = hiltViewModel(), navigateUp = navigateUp, onRecommendedClick = onRecommendedClick)
}

@Composable
internal fun MediaInfo(viewModel: MediaInfoViewModel, navigateUp: () -> Unit, onRecommendedClick: (media: Multi) -> Unit) {
    val viewState by viewModel.state.collectAsState()
    if (viewState.mediaType == Multi.MediaType.MOVIE) {
        val recommended = viewModel.getRecommendedMovies(viewState.mediaId).collectAsLazyPagingItems()
        MediaInfo(
            viewState = viewState,
            recommended = recommended,
            onWatchlistAction = viewModel::watchlistAction,
            onWatchedAction = viewModel::watchStateAction,
            onRecommendedClick = onRecommendedClick
        )
    } else if (viewState.mediaType == Multi.MediaType.TV_SERIES) {
        val recommended = viewModel.getRecommendedShows(viewState.mediaId).collectAsLazyPagingItems()
        MediaInfo(
            viewState = viewState,
            recommended = recommended,
            onWatchlistAction = viewModel::watchlistAction,
            onWatchedAction = viewModel::watchStateAction,
            onRecommendedClick = onRecommendedClick,
            onSeasonSelected = viewModel::selectSeason,
            onEpisodeWatchAction = { episode, isWatched ->
                viewModel.episodeWatchStateAction(episodeId = episode.id.toString(), isWatched)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun <T : Multi> MediaInfo(
    viewState: MediaInfoViewState,
    recommended: LazyPagingItems<T>,
    onWatchlistAction: (checked: Boolean, media: DBMedia) -> Unit = { _, _ -> },
    onWatchedAction: (checked: Boolean, media: DBMedia) -> Unit = { _, _ -> },
    onRecommendedClick: (media: T) -> Unit = {},
    onSeasonSelected: (Int) -> Unit = {},
    onEpisodeWatchAction: (episode: Episode, isWatched: Boolean) -> Unit = { _, _ -> }
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            if (viewState.mediaType == Multi.MediaType.MOVIE) {
                CommonAppBar(withTitle = viewState.movie.title ?: "", scrollBehavior = scrollBehavior)
            } else if (viewState.mediaType == Multi.MediaType.TV_SERIES) {
                CommonAppBar(withTitle = viewState.tv.name ?: "", scrollBehavior = scrollBehavior)
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        Surface(modifier = Modifier.fillMaxWidth()) {
            MediaInfoContent(
                viewState = viewState,
                recommended = recommended,
                listState = listState,
                contentPadding = contentPadding,
                onWatchlistAction = onWatchlistAction,
                onWatchedAction = onWatchedAction,
                onRecommendedClick = onRecommendedClick,
                onSeasonSelected = onSeasonSelected,
                onEpisodeWatchAction = onEpisodeWatchAction,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
internal fun <T : Multi> MediaInfoContent(
    viewState: MediaInfoViewState,
    recommended: LazyPagingItems<T>,
    listState: LazyListState,
    contentPadding: PaddingValues,
    onWatchlistAction: (checked: Boolean, media: DBMedia) -> Unit = { _, _ -> },
    onWatchedAction: (checked: Boolean, media: DBMedia) -> Unit = { _, _ -> },
    onRecommendedClick: (media: T) -> Unit = {},
    onSeasonSelected: (Int) -> Unit = {},
    onEpisodeWatchAction: (episode: Episode, isWatched: Boolean) -> Unit = { _, _ -> },
    modifier: Modifier
) {
    val gutter = Layout.gutter
    val bodyMargin = Layout.bodyMargin

    LazyColumn(state = listState, contentPadding = contentPadding, modifier = modifier) {
        item {
            Backdrop(
                backdropPath = when (viewState.mediaType) {
                    Multi.MediaType.MOVIE -> {
                        viewState.movie.backdropPath
                    }
                    Multi.MediaType.TV_SERIES -> {
                        viewState.tv.backdropPath
                    }
                    else -> {
                        null
                    }
                },
                modifier = Modifier
                    .padding(horizontal = bodyMargin, vertical = gutter)
                    .fillMaxWidth()
                    .aspectRatio(16f / 10)
            )
        }
        item(key = "overview") {
            OverviewContent(
                movie = viewState.movie,
                tv = viewState.tv,
                isInWatchlist = viewState.isInWatchlist,
                isWatched = viewState.isWatched,
                modifier = Modifier.fillMaxWidth(),
                onWatchlistAction = onWatchlistAction,
                onWatchedAction = onWatchedAction
            )
        }

        if (viewState.mediaType == Multi.MediaType.TV_SERIES) {
            item(key = "seasons") {
                Seasons(
                    tv = viewState.tv,
                    season = viewState.seasonInfo,
                    watchedEpisodes = viewState.media.watchStatus,
                    onSeasonSelected = onSeasonSelected,
                    onWatchClicked = onEpisodeWatchAction
                )
            }
        }

        item(key = "cast") {
            viewState.credits
                .composeWhen(success = { credits ->
                    credits.cast?.let { castList ->
                        PersonRow(items = castList, title = "Cast", modifier = Modifier)
                    }
                })
                .composeWhen(loading = {
                    PersonRow(items = emptyList(), title = "Cast", refreshing = true)
                })
        }

        item(key = "crew") {
            viewState.credits
                .composeWhen(success = { credits ->
                    credits.crew?.let { crewList ->
                        PersonRow(items = crewList, title = "Crew", modifier = Modifier)
                    }
                })
                .composeWhen(loading = {
                    PersonRow(items = emptyList(), title = "Crew", refreshing = true)
                })
        }

        if (viewState.mediaType == Multi.MediaType.MOVIE) {
            item(key = "rec-movies") {
                PagingCarousel(
                    items = recommended,
                    title = "Recommended Movies",
                    refreshing = false,
                    modifier = Modifier.fillMaxWidth(),
                    onItemClick = { t, _ ->
                        onRecommendedClick(t)
                    },
                    onMoreClick = null
                )
            }
        } else if (viewState.mediaType == Multi.MediaType.TV_SERIES) {
            item(key = "rec-tv") {
                PagingCarousel(
                    items = recommended,
                    title = "Recommended TV Series",
                    refreshing = false,
                    modifier = Modifier.fillMaxWidth(),
                    onItemClick = { t, _ ->
                        onRecommendedClick(t)
                    },
                    onMoreClick = null
                )
            }
        }
        item {
            ProvideTextStyle(value = ubuntuTypography.titleSmall.copy(fontSize = 10.sp)) {
                Text(
                    text = "Media ID: ${viewState.mediaId}",
                    modifier = Modifier.padding(
                        horizontal = bodyMargin,
                        vertical = gutter
                    )
                )
            }
        }
        item(key = "spacer") {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
