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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
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
            viewModel.setEmpty(movieItems.itemCount == 0 || viewState.query.getQuery().isBlank())
        }

        MediaType.SHOW -> {
            viewModel.setLoading(tvItems.loadState.refresh == LoadState.Loading)
            viewModel.setEmpty(tvItems.itemCount == 0 || viewState.query.getQuery().isBlank())
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
    val searchHeight = TextFieldDefaults.MinHeight + 32.dp
    val searchHeightPx = with(LocalDensity.current) { searchHeight.roundToPx().toFloat() }
    val searchHeightOffset = remember { mutableFloatStateOf(0f) }
    var searchText by rememberSaveable { mutableStateOf(state.query.getQuery()) }

    val nsc = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val newOffset = searchHeightOffset.floatValue + available.y
                searchHeightOffset.floatValue = newOffset.coerceIn(-searchHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        Column(modifier = Modifier.fillMaxWidth()) {
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
                    Text(text = "Search movie or tv show...")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
            ) {}

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
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
                                        .animateItemPlacement()
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
                                        .animateItemPlacement()
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

/**
 * [OutlinedTextField] with Validation
 * TODO Extract Composable
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchTextInput(
    modifier: Modifier = Modifier,
    label: String,
    hint: String = "",
    prefillValue: String = "",
    errorText: String = "",
    showLabel: Boolean = true,
    keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
    keyboardActions: KeyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
    validate: (String) -> Boolean = { true },
    onChange: (String) -> Unit,
    onError: (String) -> Unit = {},
) {
    var value by remember { mutableStateOf(prefillValue) }
    var error by remember { mutableStateOf(false) }
    Column {
        OutlinedTextField(
            leadingIcon = {
                Icon(imageVector = Icons.Rounded.Search, contentDescription = "Search")
            },
            trailingIcon = {
                if (value.isNotBlank()) {
                    IconButton(onClick = {
                        value = ""
                    }) {
                        Icon(imageVector = Icons.Rounded.Clear, contentDescription = "Clear")
                    }
                }
            },
            placeholder = { Text(text = hint) },
            value = value,
            onValueChange = {
                value = it // always update state
                when {
                    validate(it) || it.isBlank() -> {
                        error = false
                        onChange(it)
                    }

                    else -> {
                        error = true
                        onError(it)
                    }
                }
            },
            isError = error,
            label = {
                if (showLabel) Text(text = label)
            },
            singleLine = true,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
        )
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
