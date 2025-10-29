/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import app.tivi.common.compose.ui.plus
import com.afterroot.ui.common.compose.components.FilterChipGroup
import com.afterroot.ui.common.compose.components.MediaCard
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.resources.R
import com.afterroot.watchdone.ui.common.ItemSelectedCallback
import com.afterroot.watchdone.viewmodel.SearchViewModel

@Composable
fun Search(
  viewModel: SearchViewModel = hiltViewModel(),
  itemSelectedCallback: ItemSelectedCallback<Media>,
) {
  val viewState by viewModel.state.collectAsState()
  val movieItems = viewModel.searchMovies.collectAsLazyPagingItems()
  val tvItems = viewModel.searchTV.collectAsLazyPagingItems()

  if (viewState.refresh) {
    when (viewState.mediaType) {
      MediaType.MOVIE -> movieItems.refresh()
      MediaType.SHOW -> tvItems.refresh()
      else -> {}
    }
  }

  when (viewState.mediaType) {
    MediaType.MOVIE -> {
      viewModel.setLoading(movieItems.loadState.refresh == LoadState.Loading)
      viewModel.setEmpty(movieItems.itemCount == 0 && viewState.query.getQuery().isNotBlank())
    }

    MediaType.SHOW -> {
      viewModel.setLoading(tvItems.loadState.refresh == LoadState.Loading)
      viewModel.setEmpty(tvItems.itemCount == 0 && viewState.query.getQuery().isNotBlank())
    }

    else -> {}
  }

  Search(
    state = viewState,
    itemSelectedCallback = itemSelectedCallback,
    movieItems = movieItems,
    tvItems = tvItems,
    onSearch = {
      if (it.isNotBlank()) {
        viewModel.search(query = viewState.query.query(it))
      }
    },
    onMovieSelected = {
      viewModel.setMediaType(MediaType.MOVIE)
    },
    onTVSelected = {
      viewModel.setMediaType(MediaType.SHOW)
    },
  )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun Search(
  state: SearchViewState,
  itemSelectedCallback: ItemSelectedCallback<Media>,
  movieItems: LazyPagingItems<Media>,
  tvItems: LazyPagingItems<Media>,
  onSearch: (String) -> Unit = {},
  onMovieSelected: () -> Unit = {},
  onTVSelected: () -> Unit = {},
) {
  var searchQuery by remember { mutableStateOf(state.query) }
  val listState = rememberLazyGridState()
  var searchText by rememberSaveable { mutableStateOf(state.query.getQuery()) }

  Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
    Column(modifier = Modifier.fillMaxWidth()) {
      // TODO Migrate to new overload
      DockedSearchBar(
        query = searchText,
        onQueryChange = {
          searchText = it
          searchQuery = searchQuery.query(it)
          onSearch(searchQuery.getQuery())
        },
        onSearch = {
          searchQuery = searchQuery.query(it)
          onSearch(searchQuery.getQuery())
        },
        active = false,
        onActiveChange = {},
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
        placeholder = {
          Text(text = "Search ${state.mediaType?.value}...")
        },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
          .padding(top = 8.dp),
      ) {}

      Surface(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .align(Alignment.CenterHorizontally)
          .padding(4.dp),
      ) {
        SearchChips(
          preselect = state.mediaType ?: MediaType.MOVIE,
          onMovieSelected = onMovieSelected,
          onTVSelected = onTVSelected,
          modifier = Modifier.padding(horizontal = 8.dp),
        )
      }
    }
  }) { paddingValues ->
    Box(modifier = Modifier.fillMaxSize()) {
      AnimatedVisibility(
        visible = state.isLoading && !state.empty,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.align(Alignment.Center),
      ) {
        CircularProgressIndicator()
      }

      AnimatedVisibility(
        visible = state.empty,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.align(Alignment.Center),
      ) {
        Text(text = "No results found for ${state.mediaType?.value} $searchText")
      }

      AnimatedVisibility(
        visible = !state.isLoading || state.empty,
        enter = fadeIn(),
        exit = fadeOut(),
      ) {
        LazyVerticalGrid(
          state = listState,
          columns = GridCells.Fixed(3),
          contentPadding = paddingValues + PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier
            // .nestedScroll(nsc)
            .fillMaxHeight(),
        ) {
          if (state.mediaType == MediaType.MOVIE) {
            items(
              count = movieItems.itemCount,
              key = movieItems.itemKey { it.id },
            ) { index ->
              val movie = movieItems[index]
              if (movie != null) {
                MediaCard(
                  media = movie,
                  onClick = {
                    itemSelectedCallback.onClick(index, null, movie)
                  },
                  modifier = Modifier
                    .animateItem()
                    .fillMaxWidth()
                    .aspectRatio(2 / 3f),
                )
              }
            }
          } else if (state.mediaType == MediaType.SHOW) {
            items(count = tvItems.itemCount, key = tvItems.itemKey { it.id }) { index ->
              val tv = tvItems[index]
              if (tv != null) {
                MediaCard(
                  media = tv,
                  onClick = {
                    itemSelectedCallback.onClick(index, null, tv)
                  },
                  modifier = Modifier
                    .animateItem()
                    .fillMaxWidth()
                    .aspectRatio(2 / 3f),
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun SearchChips(
  preselect: MediaType,
  onMovieSelected: () -> Unit,
  onTVSelected: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val movieText = stringResource(id = R.string.text_search_movies)
  val showText = stringResource(id = R.string.text_search_show)
  FilterChipGroup(
    modifier = modifier,
    chipSpacing = 8.dp,
    icons = listOf(Icons.Outlined.Movie, Icons.Outlined.Tv),
    list = listOf(movieText, showText),
    preSelect = listOf(if (preselect == MediaType.MOVIE) movieText else showText),
    onSelectedChanged = { selected, _ ->
      when (selected) {
        movieText -> onMovieSelected()
        showText -> onTVSelected()
      }
    },
  )
}
