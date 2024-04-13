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
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import app.tivi.common.compose.Layout
import app.tivi.common.compose.bodyWidth
import app.tivi.common.compose.fullSpanItem
import app.tivi.common.compose.ui.plus
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.afterroot.ui.common.compose.components.AssistChip
import com.afterroot.ui.common.compose.components.BasePosterCard
import com.afterroot.ui.common.compose.components.CommonAppBar
import com.afterroot.ui.common.compose.components.DynamicChipGroup
import com.afterroot.ui.common.compose.components.LocalPosterSize
import com.afterroot.ui.common.compose.components.LocalTMDbBaseUrl
import com.afterroot.ui.common.compose.theme.ListStyleWatchlistItemShape
import com.afterroot.ui.common.compose.theme.ubuntuTypography
import com.afterroot.ui.common.compose.utils.CenteredRow
import com.afterroot.ui.common.compose.utils.TopBarWindowInsets
import com.afterroot.ui.common.compose.utils.topAppBarScrollBehavior
import com.afterroot.watchdone.base.WatchlistType
import com.afterroot.watchdone.data.model.Filters
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.data.model.WatchStateValues
import com.afterroot.watchdone.ui.common.ItemSelectedCallback
import com.afterroot.watchdone.ui.media.MediaTypeFilter
import com.afterroot.watchdone.ui.media.MetaText
import com.afterroot.watchdone.resources.R as CommonR

@Composable
fun Watchlist(
  viewModel: WatchlistViewModel = viewModel(),
  settingsAction: () -> Unit,
  itemSelectedCallback: ItemSelectedCallback<Media>,
) {
  val state by viewModel.state.collectAsState()
  val watchlist = viewModel.watchlist.collectAsLazyPagingItems()

  Watchlist(
    state = state.copy(loading = watchlist.loadState.refresh == LoadState.Loading),
    watchlist = watchlist,
    itemSelectedCallback = itemSelectedCallback,
    refresh = {
      watchlist.refresh()
    },
    sortAction = {
      viewModel.setSort(!state.sortAscending)
    },
    settingsAction = settingsAction,
    watchlistTypeAction = {
      viewModel.setWatchlistType(it)
    },
    filter = {
      viewModel.updateFilters(it)
      watchlist.refresh()
    },
  )
}

@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalMaterialApi::class,
)
@Composable
private fun Watchlist(
  state: WatchlistState,
  watchlist: LazyPagingItems<Media>,
  itemSelectedCallback: ItemSelectedCallback<Media>,
  refresh: () -> Unit,
  sortAction: () -> Unit,
  settingsAction: () -> Unit,
  watchlistTypeAction: (WatchlistType) -> Unit,
  filter: (Filters) -> Unit = {},
) {
  val listState = rememberLazyGridState()

  Scaffold(
    topBar = {
      Column {
        CommonAppBar(
          withTitle = stringResource(id = CommonR.string.title_watchlist),
          scrollBehavior = topAppBarScrollBehavior(),
          windowInsets = TopBarWindowInsets,
          actions = {
            IconButton(onClick = { settingsAction() }) {
              Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = stringResource(
                  id = CommonR.string.title_settings,
                ),
              )
            }
          },
          navigationIcon = {
            when (state.watchlistType) {
              WatchlistType.GRID -> {
                IconButton(onClick = { watchlistTypeAction(WatchlistType.LIST) }) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ListAlt,
                    contentDescription = stringResource(
                      id = CommonR.string.watchlist_style,
                    ),
                  )
                }
              }

              WatchlistType.LIST -> {
                IconButton(onClick = { watchlistTypeAction(WatchlistType.GRID) }) {
                  Icon(
                    imageVector = Icons.Outlined.GridView,
                    contentDescription = stringResource(
                      id = CommonR.string.watchlist_style,
                    ),
                  )
                }
              }
            }
          },
        )
        FiltersRow(
          state = state,
          sortAction = sortAction,
          refresh = refresh,
          filter = filter,
          modifier = Modifier.fillMaxWidth(),
        )
      }
    },
    modifier = Modifier.fillMaxSize(),
  ) { paddingValues ->
    val refreshState = rememberPullRefreshState(
      refreshing = state.loading,
      onRefresh = refresh,
    )

    val bodyMargin = Layout.bodyMargin
    val gutter = Layout.gutter

    Box(
      modifier = Modifier
        .pullRefresh(state = refreshState)
        .fillMaxWidth(),
    ) {
      if (watchlist.itemCount != 0) {
        LazyVerticalGrid(
          state = listState,
          columns = GridCells.Fixed(
            when (state.watchlistType) {
              WatchlistType.GRID -> Layout.gridColumns
              WatchlistType.LIST -> Layout.listColumns
            },
          ),
          contentPadding = paddingValues + PaddingValues(horizontal = bodyMargin, vertical = gutter),
          horizontalArrangement = Arrangement.spacedBy(gutter),
          verticalArrangement = Arrangement.spacedBy(gutter),
          modifier = Modifier
            .bodyWidth()
            .fillMaxHeight(),
        ) {
          items(
            count = watchlist.itemCount,
            key = watchlist.itemKey { item ->
              item.tmdbId ?: item.id
            },
          ) { index ->
            watchlist[index]?.let {
              WatchlistItem(
                item = it,
                type = state.watchlistType,
                itemSelectedCallback = itemSelectedCallback,
              )
            }
          }

          if (watchlist.loadState.append == LoadState.Loading) {
            fullSpanItem {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(24.dp),
              ) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
              }
            }
          }
        }
      }

      PullRefreshIndicator(
        refreshing = state.loading,
        state = refreshState,
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(paddingValues),
        scale = true,
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersRow(
  modifier: Modifier = Modifier,
  state: WatchlistState,
  sortAction: () -> Unit,
  filter: (Filters) -> Unit = {},
  refresh: () -> Unit,
) {
  val scrollState = rememberScrollState()
  val bodyMargin = Layout.bodyMargin

  Surface(color = MaterialTheme.colorScheme.surface, modifier = modifier) {
    Row(
      modifier = Modifier.horizontalScroll(scrollState),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Spacer(modifier = Modifier.size(bodyMargin - 8.dp))

      AssistChip(
        text = if (state.sortAscending) {
          stringResource(id = CommonR.string.text_ascending)
        } else {
          stringResource(id = CommonR.string.text_descending)
        },
        leadingIcon = {
          Icon(
            imageVector = if (state.sortAscending) Icons.Rounded.ArrowDownward else Icons.Rounded.ArrowUpward,
            contentDescription = "Sort Icon",
            modifier = Modifier.size(FilterChipDefaults.IconSize),
            tint = MaterialTheme.colorScheme.onSurface,
          )
        },
      ) {
        sortAction()
        refresh()
      }

      VerticalDivider(modifier = Modifier.height(FilterChipDefaults.Height))

      CenteredRow {
        Icon(
          imageVector = Icons.Rounded.FilterAlt,
          contentDescription = "Filter Icon",
          modifier = Modifier.padding(2.dp),
          tint = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.width(4.dp))

        MediaTypeFilter(
          modifier = Modifier,
          preSelect = state.filters.mediaType,
          showOnlySelected = true,
          onSelectionCleared = {
            filter(state.filters.copy(mediaType = null))
          },
        ) { selectedMediaType: MediaType ->
          filter(state.filters.copy(mediaType = selectedMediaType))
        }

        Spacer(modifier = Modifier.width(8.dp))

        FilterChips(
          modifier = Modifier,
          preSelect = when (state.filters.watchState) {
            WatchStateValues.WATCHED -> stringResource(
              id = CommonR.string.watch_state_watched,
            )

            WatchStateValues.PENDING -> stringResource(
              id = CommonR.string.watch_state_pending,
            )

            WatchStateValues.STARTED -> stringResource(
              id = CommonR.string.watch_state_started,
            )

            else -> null
          },
        ) { _, selectedList ->
          if (selectedList.isEmpty()) {
            filter(state.filters.copy(watchState = null))
            return@FilterChips
          }
          filter(
            state.filters.copy(
              watchState = WatchStateValues.entries[selectedList[0]],
            ),
          )
        }

        Spacer(modifier = Modifier.size(16.dp))
      }
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyGridItemScope.WatchlistItem(
  item: Media,
  type: WatchlistType,
  itemSelectedCallback: ItemSelectedCallback<Media>,
) {
  when (item.mediaType) {
    MediaType.MOVIE -> {
      when (type) {
        WatchlistType.LIST -> {
          ListWatchlistItem(
            poster = item.posterPath,
            title = item.title,
            rating = item.rating,
            releaseDate = item.releaseDate,
            modifier = Modifier
              .animateItemPlacement()
              .fillMaxWidth(),
            isWatched = item.isWatched,
            mediaType = MediaType.MOVIE,
            onClick = { itemSelectedCallback.onClick(0, null, item) },
          )
        }

        WatchlistType.GRID -> {
          GridWatchlistItem(
            poster = item.posterPath,
            title = item.title,
            rating = item.rating,
            releaseDate = item.releaseDate,
            modifier = Modifier
              .animateItemPlacement()
              .fillMaxWidth()
              .aspectRatio(2 / 3f),
            isWatched = item.isWatched,
            mediaType = MediaType.MOVIE,
            onClick = { itemSelectedCallback.onClick(0, null, item) },
          )
        }
      }
    }

    MediaType.SHOW -> {
      when (type) {
        WatchlistType.LIST -> {
          ListWatchlistItem(
            poster = item.posterPath,
            title = item.title,
            rating = item.rating,
            releaseDate = item.releaseDate,
            modifier = Modifier
              .animateItemPlacement()
              .fillMaxWidth(),
            isWatched = item.isWatched,
            mediaType = MediaType.SHOW,
            onClick = { itemSelectedCallback.onClick(0, null, item) },
          )
        }

        WatchlistType.GRID -> {
          GridWatchlistItem(
            poster = item.posterPath,
            title = item.title,
            rating = item.rating,
            releaseDate = item.releaseDate,
            modifier = Modifier
              .animateItemPlacement()
              .fillMaxWidth()
              .aspectRatio(2 / 3f),
            isWatched = item.isWatched,
            mediaType = MediaType.SHOW,
            onClick = { itemSelectedCallback.onClick(0, null, item) },
          )
        }
      }
    }

    else -> {
      // BLANK
    }
  }
}

@Composable
fun ListWatchlistItem(
  poster: String?,
  title: String?,
  rating: Float?,
  releaseDate: String?,
  mediaType: MediaType,
  modifier: Modifier = Modifier,
  isWatched: Boolean = false,
  onClick: (() -> Unit)? = {},
) {
  Surface(
    shape = ListStyleWatchlistItemShape,
    color = MaterialTheme.colorScheme.surface,
    modifier = modifier,
  ) {
    Row(
      modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier,
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      poster?.let {
        BasePosterCard(
          modifier = Modifier
            .height(128.dp)
            .aspectRatio(2 / 3f),
          title = title,
          posterPath = poster,
        )
      }

      Spacer(modifier = Modifier.size(16.dp))

      Column(
        modifier = Modifier
          .weight(1f)
          .align(Alignment.CenterVertically),
      ) {
        title?.let {
          ProvideTextStyle(value = ubuntuTypography.bodyMedium) {
            MetaText(
              text = it,
              icon = when (mediaType) {
                MediaType.MOVIE -> Icons.Rounded.Movie
                MediaType.SHOW -> Icons.Rounded.LiveTv
                else -> null
              },
            ) {
              Text(text = it, softWrap = false, overflow = TextOverflow.Ellipsis)
            }
          }
        }
        releaseDate?.let {
          ProvideTextStyle(value = ubuntuTypography.labelSmall) {
            MetaText(text = it, icon = Icons.Rounded.Event) {
              Text(text = it)
            }
          }
        }
        rating?.let {
          ProvideTextStyle(value = ubuntuTypography.bodySmall) {
            MetaText(text = "%.1f".format(it), icon = Icons.Rounded.Star) {
              Text(text = it)
            }
          }
        }
      }

      Spacer(modifier = Modifier.size(16.dp))

      if (isWatched) {
        Icon(
          imageVector = Icons.Rounded.Done,
          contentDescription = "Watched",
          modifier = Modifier.align(Alignment.CenterVertically),
        )

        Spacer(modifier = Modifier.size(16.dp))
      }
    }
  }
}

@Composable
fun GridWatchlistItem(
  poster: String?,
  title: String?,
  rating: Float?,
  releaseDate: String?,
  mediaType: MediaType,
  modifier: Modifier = Modifier,
  shape: Shape = MaterialTheme.shapes.medium,
  isWatched: Boolean = false,
  onClick: (() -> Unit)? = {},
) {
  Surface(
    color = MaterialTheme.colorScheme.onSurface
      .copy(alpha = 0.2f)
      .compositeOver(MaterialTheme.colorScheme.surface),
    shape = shape,
    modifier = modifier,
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
    ) {
      poster?.let {
        AsyncImage(
          model = ImageRequest.Builder(LocalContext.current)
            .data(
              LocalTMDbBaseUrl.current + LocalPosterSize.current + poster,
            ).crossfade(true).build(),
          contentDescription = title,
          contentScale = ContentScale.Crop,
          modifier = Modifier.matchParentSize(),
        )
      }
      Box(
        modifier = Modifier
          .background(
            brush = Brush.verticalGradient(
              listOf(
                Color(0xB3000000),
                Color.Transparent,
                Color(0xB3000000),
              ),
            ),
          )
          .padding(all = 8.dp)
          .matchParentSize(),
      ) {
        Row(
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth(),
        ) {
          if (mediaType == MediaType.MOVIE) {
            Icon(
              imageVector = Icons.Rounded.Movie,
              contentDescription = stringResource(
                id = CommonR.string.text_search_movies,
              ),
              modifier = Modifier.align(Alignment.CenterVertically),
              tint = Color.White,
            )
          } else {
            Icon(
              imageVector = Icons.Rounded.LiveTv,
              contentDescription = stringResource(
                id = CommonR.string.text_search_show,
              ),
              modifier = Modifier.align(Alignment.CenterVertically),
              tint = Color.White,
            )
          }
          if (isWatched) {
            Icon(
              imageVector = Icons.Rounded.Done,
              contentDescription = "Watched",
              modifier = Modifier.align(Alignment.CenterVertically),
              tint = Color.White,
            )
          }
        }
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomStart),
        ) {
          title?.let {
            ProvideTextStyle(value = ubuntuTypography.bodySmall) {
              Text(text = it, softWrap = false, overflow = TextOverflow.Ellipsis)
            }
          }
          Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
          ) {
            releaseDate?.let {
              ProvideTextStyle(value = ubuntuTypography.bodySmall) {
                Text(text = it, modifier = Modifier.align(Alignment.Bottom))
              }
            }
            rating?.let {
              MetaText(
                text = "%.1f".format(it),
                modifier = Modifier.align(Alignment.Bottom),
                icon = Icons.Rounded.Star,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun FilterChips(
  modifier: Modifier = Modifier,
  preSelect: String? = null,
  onSelectionChanged: (index: Int, selectedList: List<Int>) -> Unit,
) {
  DynamicChipGroup(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    list = listOf(
      stringResource(id = CommonR.string.watch_state_watched),
      stringResource(id = CommonR.string.watch_state_pending),
      stringResource(id = CommonR.string.watch_state_started),
    ),
    preSelectItem = remember { preSelect },
    onSelectedChanged = { index, _, _, _, selectedList ->
      onSelectionChanged(index, selectedList)
    },
    showOnlySelected = true,
  ) { _, title, icon, selected, onClick, clear ->
    FilterChip(
      selected = selected,
      onClick = { onClick(selected) },
      label = { Text(text = title) },
      leadingIcon = {
        if (icon != null) {
          Icon(
            imageVector = icon,
            contentDescription = "$title Icon",
            modifier = Modifier.size(FilterChipDefaults.IconSize),
            tint = MaterialTheme.colorScheme.onSurface,
          )
        }
      },
      trailingIcon = {
        if (selected) {
          IconButton(modifier = Modifier.size(InputChipDefaults.IconSize), onClick = {
            clear()
          }) {
            Icon(
              imageVector = Icons.Rounded.Clear,
              contentDescription = stringResource(
                id = CommonR.string.content_desc_clear_filter,
              ),
              modifier = Modifier.size(FilterChipDefaults.IconSize),
              tint = MaterialTheme.colorScheme.onSurface,
            )
          }
        }
      },
    )
  }
}
