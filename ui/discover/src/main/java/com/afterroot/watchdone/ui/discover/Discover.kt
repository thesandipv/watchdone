/*
 * Copyright (C) 2020-2024 Sandip Vaghela
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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.afterroot.ui.common.compose.components.CommonAppBar
import com.afterroot.ui.common.compose.components.PagingCarousel
import com.afterroot.ui.common.compose.utils.TopBarWindowInsets
import com.afterroot.ui.common.compose.utils.topAppBarScrollBehavior
import com.afterroot.watchdone.data.compoundmodel.DiscoverEntryWithMedia
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.ui.common.ItemSelectedCallback
import com.afterroot.watchdone.ui.media.MediaTypeFilter
import com.afterroot.watchdone.viewmodel.DiscoverViewModel

@Composable
fun DiscoverChips(
  onMovieSelected: () -> Unit,
  onShowSelected: () -> Unit,
  modifier: Modifier = Modifier,
  currentMediaType: MediaType = MediaType.MOVIE,
) {
  MediaTypeFilter(
    modifier = modifier,
    preSelect = currentMediaType,
    showOnlySelected = false,
  ) { selectedMediaType: MediaType ->
    when (selectedMediaType) {
      MediaType.MOVIE -> onMovieSelected()
      MediaType.SHOW -> onShowSelected()
      else -> {
        // DO NOTHING
      }
    }
  }
}

@Composable
fun Discover(
  discoverViewModel: DiscoverViewModel = hiltViewModel(),
  itemSelectedCallback: ItemSelectedCallback<Media>,
) {
  val viewState by discoverViewModel.state.collectAsState()
  val discoverItems = discoverViewModel.pagedDiscoverList.collectAsLazyPagingItems()
  val nowPlayingItems = discoverViewModel.pagedNowPlayingList.collectAsLazyPagingItems()
  val nowPlayingShowItems = discoverViewModel.pagedDiscoverOnTv.collectAsLazyPagingItems()
  val topRatedItems = discoverViewModel.pagedTopRated.collectAsLazyPagingItems()

  Discover(
    state = viewState,
    popularItems = discoverItems,
    nowPlayingItems = nowPlayingItems,
    nowPlayingShowItems = nowPlayingShowItems,
    topRatedItems = topRatedItems,
    itemSelectedCallback = itemSelectedCallback,
    onMovieChipSelected = { discoverViewModel.setMediaType(MediaType.MOVIE) },
    onShowChipSelected = { discoverViewModel.setMediaType(MediaType.SHOW) },
    refresh = {
      discoverItems.refresh()
      topRatedItems.refresh()

      when (viewState.mediaType) {
        MediaType.MOVIE -> {
          nowPlayingItems.refresh()
        }

        MediaType.SHOW -> {
          nowPlayingShowItems.refresh()
        }

        else -> {}
      }
    },
  )
}

@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalMaterialApi::class,
)
@Composable
internal fun Discover(
  state: DiscoverViewState,
  popularItems: LazyPagingItems<DiscoverEntryWithMedia>,
  nowPlayingItems: LazyPagingItems<DiscoverEntryWithMedia>,
  nowPlayingShowItems: LazyPagingItems<DiscoverEntryWithMedia>,
  topRatedItems: LazyPagingItems<DiscoverEntryWithMedia>,
  itemSelectedCallback: ItemSelectedCallback<Media>,
  onMovieChipSelected: () -> Unit,
  onShowChipSelected: () -> Unit,
  refresh: () -> Unit,
) {
  val listState = rememberLazyListState()

  Scaffold(
    topBar = {
      CommonAppBar(
        withTitle = "Discover",
        scrollBehavior = topAppBarScrollBehavior(),
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
      if ((
        state.mediaType == MediaType.MOVIE &&
          popularItems.itemCount != 0 ||
          state.mediaType == MediaType.SHOW &&
          popularItems.itemCount != 0
        )
      ) {
        LazyColumn(
          state = listState,
          contentPadding = paddingValues, // Padding to be handled by child
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxHeight(),
        ) {
          item {
            DiscoverChips(
              onMovieSelected = { onMovieChipSelected() },
              onShowSelected = { onShowChipSelected() },
              modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
              currentMediaType = state.mediaType ?: MediaType.MOVIE,
            )
          }
          if (state.mediaType == MediaType.MOVIE) {
            item {
              PagingCarousel(
                items = nowPlayingItems,
                title = "Now Playing",
                refreshing = nowPlayingItems.loadState.refresh == LoadState.Loading,
                modifier = Modifier.fillMaxWidth(),
                onItemClick = { media, _ ->
                  itemSelectedCallback.onClick(0, null, media)
                },
              )
            }
          } else { // MediaType.SHOW
            item {
              PagingCarousel(
                items = nowPlayingShowItems,
                title = "Now On TV",
                refreshing = nowPlayingShowItems.loadState.refresh == LoadState.Loading,
                modifier = Modifier.fillMaxWidth(),
                onItemClick = { media, _ ->
                  itemSelectedCallback.onClick(0, null, media)
                },
              )
            }
          }

          item {
            PagingCarousel(
              items = popularItems,
              title = "Popular",
              refreshing = popularItems.loadState.refresh == LoadState.Loading,
              modifier = Modifier.fillMaxWidth(),
              onItemClick = { media, _ ->
                itemSelectedCallback.onClick(0, null, media)
              },
            )
          }

          item {
            PagingCarousel(
              items = topRatedItems,
              title = "Top Rated",
              refreshing = topRatedItems.loadState.refresh == LoadState.Loading,
              modifier = Modifier.fillMaxWidth(),
              onItemClick = { media, _ ->
                itemSelectedCallback.onClick(0, null, media)
              },
            )
          }

          item {
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
