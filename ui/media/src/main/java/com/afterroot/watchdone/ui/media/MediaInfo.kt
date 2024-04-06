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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NavigateBefore
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import app.tivi.common.compose.Layout
import com.afterroot.ui.common.compose.components.Backdrop
import com.afterroot.ui.common.compose.components.CommonAppBar
import com.afterroot.ui.common.compose.components.PagingCarousel
import com.afterroot.ui.common.compose.theme.ubuntuTypography
import com.afterroot.ui.common.compose.utils.TopBarWindowInsets
import com.afterroot.ui.common.compose.utils.topAppBarScrollBehavior
import com.afterroot.watchdone.data.compoundmodel.RecommendedEntryWithMedia
import com.afterroot.watchdone.data.model.DBMedia
import com.afterroot.watchdone.data.model.Episode
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.viewmodel.MediaInfoViewModel

@Composable
fun MediaInfo(
  navigateUp: () -> Unit,
  onRecommendedClick: (media: Media) -> Unit,
  onWatchProviderClick: (link: String) -> Unit = { _ -> },
  shareToIG: ((mediaId: Int, poster: String) -> Unit)? = null,
) {
  MediaInfo(
    viewModel = hiltViewModel(),
    navigateUp = navigateUp,
    onRecommendedClick = onRecommendedClick,
    onWatchProviderClick = onWatchProviderClick,
    shareToIG = shareToIG,
  )
}

@Composable
internal fun MediaInfo(
  viewModel: MediaInfoViewModel,
  navigateUp: () -> Unit,
  onRecommendedClick: (media: Media) -> Unit,
  onWatchProviderClick: (link: String) -> Unit = { _ -> },
  shareToIG: ((mediaId: Int, poster: String) -> Unit)? = null,
) {
  val viewState by viewModel.state.collectAsState()
  val recommended = viewModel.pagedRecommendedList.collectAsLazyPagingItems()

  if (viewState.mediaType == MediaType.MOVIE) {
    MediaInfo(
      viewState = viewState,
      recommended = recommended,
      onWatchlistAction = viewModel::watchlistAction,
      onWatchedAction = viewModel::watchStateAction,
      onRecommendedClick = onRecommendedClick,
      onWatchProviderClick = onWatchProviderClick,
      shareToIG = shareToIG,
      navigateUp = navigateUp,
    )
  } else if (viewState.mediaType == MediaType.SHOW) {
    MediaInfo(
      viewState = viewState,
      recommended = recommended,
      onWatchlistAction = viewModel::watchlistAction,
      onWatchedAction = viewModel::watchStateAction,
      onRecommendedClick = onRecommendedClick,
      onSeasonSelected = viewModel::selectSeason,
      onEpisodeWatchAction = { episode, isWatched ->
        viewModel.episodeWatchStateAction(episodeId = episode.id.toString(), isWatched)
      },
      onWatchProviderClick = onWatchProviderClick,
      shareToIG = shareToIG,
      navigateUp = navigateUp,
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MediaInfo(
  viewState: MediaInfoViewState,
  recommended: LazyPagingItems<RecommendedEntryWithMedia>,
  onWatchlistAction: (checked: Boolean, media: DBMedia) -> Unit = { _, _ -> },
  onWatchedAction: (checked: Boolean, media: DBMedia) -> Unit = { _, _ -> },
  onRecommendedClick: (media: Media) -> Unit = {},
  onSeasonSelected: (Int) -> Unit = {},
  onEpisodeWatchAction: (episode: Episode, isWatched: Boolean) -> Unit = { _, _ -> },
  onWatchProviderClick: (link: String) -> Unit = { _ -> },
  shareToIG: ((mediaId: Int, poster: String) -> Unit)? = null,
  navigateUp: () -> Unit = {},
) {
  val listState = rememberLazyListState()

  Scaffold(
    topBar = {
      val title = when (viewState.mediaType) {
        MediaType.MOVIE -> {
          viewState.movie.title
        }

        MediaType.SHOW -> {
          viewState.tv.name
        }

        else -> ""
      }
      CommonAppBar(
        withTitle = title ?: "",
        scrollBehavior = topAppBarScrollBehavior(),
        windowInsets = TopBarWindowInsets,
        actions = {
          IconButton(onClick = {
            if (viewState.mediaType == MediaType.MOVIE) {
              viewState.movie.posterPath?.let { poster ->
                viewState.movie.tmdbId?.let { id ->
                  shareToIG?.invoke(id, poster)
                }
              }
            } else if (viewState.mediaType == MediaType.SHOW) {
              viewState.tv.posterPath?.let { poster ->
                viewState.tv.tmdbId?.let { id ->
                  shareToIG?.invoke(id, poster)
                }
              }
            }
          }) {
            Icon(
              imageVector = Icons.Outlined.Share,
              contentDescription = "Share",
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = {
            navigateUp()
          }) {
            Icon(
              imageVector = Icons.Outlined.NavigateBefore,
              contentDescription = "Up",
            )
          }
        },
      )
    },
    modifier = Modifier,
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
        onWatchProviderClick = onWatchProviderClick,
        modifier = Modifier.fillMaxSize(),
      )
    }
  }
}

@Composable
internal fun MediaInfoContent(
  viewState: MediaInfoViewState,
  recommended: LazyPagingItems<RecommendedEntryWithMedia>,
  listState: LazyListState,
  contentPadding: PaddingValues,
  onWatchlistAction: (checked: Boolean, media: DBMedia) -> Unit = { _, _ -> },
  onWatchedAction: (checked: Boolean, media: DBMedia) -> Unit = { _, _ -> },
  onRecommendedClick: (media: Media) -> Unit = {},
  onSeasonSelected: (Int) -> Unit = {},
  onEpisodeWatchAction: (episode: Episode, isWatched: Boolean) -> Unit = { _, _ -> },
  onWatchProviderClick: (link: String) -> Unit = { _ -> },
  modifier: Modifier,
) {
  val gutter = Layout.gutter
  val bodyMargin = Layout.bodyMargin

  LazyColumn(state = listState, contentPadding = contentPadding, modifier = modifier) {
    item {
      Backdrop(
        backdropPath = when (viewState.mediaType) {
          MediaType.MOVIE -> viewState.movie.backdropPath
          MediaType.SHOW -> viewState.tv.backdropPath
          else -> null
        },
        modifier = Modifier
          .padding(horizontal = bodyMargin, vertical = gutter)
          .fillMaxWidth()
          .aspectRatio(16f / 10),
      )
    }
    item(key = "overview") {
      OverviewContent(
        movie = viewState.movie,
        tv = viewState.tv,
        isInWatchlist = viewState.isInWatchlist,
        isWatched = viewState.isWatched,
        modifier = Modifier.fillMaxWidth(),
        watchProviders = viewState.watchProviders,
        onWatchlistAction = onWatchlistAction,
        onWatchedAction = onWatchedAction,
        onWatchProvidersClick = onWatchProviderClick,
      )
    }

    if (viewState.mediaType == MediaType.SHOW) {
      item(key = "seasons") {
        Seasons(
          tv = viewState.tv,
          season = viewState.seasonInfo,
          watchedEpisodes = viewState.media.watched,
          onSeasonSelected = onSeasonSelected,
          onWatchClicked = onEpisodeWatchAction,
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

    if (viewState.mediaType == MediaType.MOVIE) {
      item(key = "rec-movies") {
        PagingCarousel(
          items = recommended,
          title = "Recommended Movies",
          refreshing = false,
          modifier = Modifier.fillMaxWidth(),
          onItemClick = { t, _ ->
            onRecommendedClick(t)
          },
          onMoreClick = null,
        )
      }
    } else if (viewState.mediaType == MediaType.SHOW) {
      item(key = "rec-tv") {
        PagingCarousel(
          items = recommended,
          title = "Recommended TV Series",
          refreshing = false,
          modifier = Modifier.fillMaxWidth(),
          onItemClick = { t, _ ->
            onRecommendedClick(t)
          },
          onMoreClick = null,
        )
      }
    }
    item {
      ProvideTextStyle(value = ubuntuTypography.titleSmall.copy(fontSize = 10.sp)) {
        Text(
          text = "Media ID: ${viewState.mediaId} | Stream and Rent data provided by JustWatch.",
          modifier = Modifier.padding(
            horizontal = bodyMargin,
            vertical = gutter,
          ),
        )
      }
    }
  }
}
