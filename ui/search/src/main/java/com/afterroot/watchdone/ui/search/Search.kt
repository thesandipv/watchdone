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

package com.afterroot.watchdone.ui.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import app.tivi.common.compose.gridItemsIndexed
import app.tivi.common.compose.ui.plus
import com.afterroot.ui.common.compose.components.MovieCard
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.ui.common.ItemSelectedCallback
import com.afterroot.watchdone.viewmodel.SearchViewModel
import com.afterroot.watchdone.viewmodel.SearchViewState
import info.movito.themoviedbapi.model.Multi
import kotlin.math.roundToInt

@Composable
fun Search(
    viewModel: SearchViewModel = hiltViewModel(),
    itemSelectedCallback: ItemSelectedCallback<Multi>
) {
    val viewState by viewModel.state.collectAsState()
    val movieItems = viewModel.searchMovies.collectAsLazyPagingItems()

    if (viewState.refresh) {
        movieItems.refresh()
    }

    Search(
        state = viewState,
        itemSelectedCallback = itemSelectedCallback,
        movieItems = movieItems,
        onSearch = {
            viewModel.search(query = viewState.query.query(it))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun Search(
    state: SearchViewState,
    itemSelectedCallback: ItemSelectedCallback<Multi>,
    movieItems: LazyPagingItems<Movie>,
    onSearch: (String) -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var searchQuery by remember { mutableStateOf(state.query) }
    val listState = rememberLazyGridState()
    val searchHeight = TextFieldDefaults.MinHeight + 16.dp
    val searchHeightPx = with(LocalDensity.current) { searchHeight.roundToPx().toFloat() }
    val searchHeightOffset = remember { mutableStateOf(0f) }

    val nsc = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val newOffset = searchHeightOffset.value + available.y
                searchHeightOffset.value = newOffset.coerceIn(-searchHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    Scaffold(topBar = {
        SearchBar(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .offset { IntOffset(x = 0, y = searchHeightOffset.value.roundToInt()) }) {
            searchQuery = searchQuery.query(it)
            onSearch(searchQuery.getQuery())
        }
    }, modifier = Modifier.fillMaxSize()) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (movieItems.loadState.refresh == LoadState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyVerticalGrid(
                    state = listState,
                    columns = GridCells.Fixed(3),
                    contentPadding = paddingValues + PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .nestedScroll(nsc)
                        .fillMaxHeight()
                ) {
                    if (state.mediaType == Multi.MediaType.MOVIE) {
                        gridItemsIndexed(items = movieItems, key = { index, _ ->
                            index
                        }) { index, movie ->
                            if (movie != null) {
                                MovieCard(
                                    movie = movie,
                                    onClick = {
                                        itemSelectedCallback.onClick(index, null, movie)
                                    },
                                    modifier = Modifier
                                        .animateItemPlacement()
                                        .fillMaxWidth()
                                        .aspectRatio(2 / 3f)
                                )
                            }
                        }
                    } /* else if (state.mediaType == Multi.MediaType.TV_SERIES) {
                    // TODO
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
                    }*/
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(modifier: Modifier = Modifier, onChange: (String) -> Unit = {}) {
    Surface(modifier = modifier) {
        SearchTextInput(modifier = Modifier, label = "Search", showLabel = false, onChange = onChange)
    }
}

/**
 * [OutlinedTextField] with Validation
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchTextInput(
    modifier: Modifier = Modifier,
    label: String,
    maxLines: Int = 1,
    errorText: String = "",
    showLabel: Boolean = true,
    keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
    keyboardActions: KeyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
    validate: (String) -> Boolean = { true },
    onChange: (String) -> Unit,
    onError: (String) -> Unit = {}
) {
    var value by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    Column {
        OutlinedTextField(
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
            maxLines = maxLines,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions
        )
    }
}
