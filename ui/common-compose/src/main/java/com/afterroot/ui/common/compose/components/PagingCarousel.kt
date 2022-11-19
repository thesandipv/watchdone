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
package com.afterroot.ui.common.compose.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import info.movito.themoviedbapi.model.Multi

@Composable
fun <T : Multi> PagingCarousel(
    items: LazyPagingItems<T>,
    title: String,
    refreshing: Boolean,
    modifier: Modifier = Modifier,
    onItemClick: (T, Int) -> Unit,
    onMoreClick: (() -> Unit)? = null
) {
    Column(modifier) {
        if (refreshing || items.itemCount != 0) {
            Header(title = title, loading = refreshing, modifier = Modifier.fillMaxWidth()) {
                if (onMoreClick != null) {
                    TextButton(
                        onClick = onMoreClick,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.alignBy(FirstBaseline)
                    ) {
                        Text(text = "More")
                    }
                }
            }

            if (items.itemCount != 0) {
                PagingCarouselInt(
                    items = items,
                    onItemClick = onItemClick,
                    modifier = Modifier
                        .height(192.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun <T : Multi> PagingCarouselInt(
    items: LazyPagingItems<T>,
    onItemClick: (T, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    val contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)

    LazyRow(
        state = lazyListState,
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(items = items, key = { index: Int, item: T ->
            when (item) {
                is TV -> {
                    item.id
                }

                is Movie -> {
                    item.id
                }

                else -> {
                    index
                }
            }
        }, itemContent = { index, item ->
                if (item != null) {
                    PosterCard(
                        media = item,
                        onClick = { onItemClick(item, index) },
                        modifier = Modifier
                            .animateItemPlacement()
                            .fillParentMaxHeight()
                            .aspectRatio(2 / 3f)
                    )
                }
            })
    }
}
