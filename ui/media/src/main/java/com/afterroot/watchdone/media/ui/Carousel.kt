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
package com.afterroot.watchdone.media.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.afterroot.ui.common.compose.components.AutoSizedCircularProgressIndicator
import com.afterroot.ui.common.compose.components.LocalPosterSize
import com.afterroot.ui.common.compose.components.LocalTMDbBaseUrl
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import info.movito.themoviedbapi.model.Multi

@Composable
fun <T : Multi> Carousel(
    items: List<T>,
    title: String,
    refreshing: Boolean,
    modifier: Modifier = Modifier,
    onItemClick: (T, Int) -> Unit,
    onMoreClick: () -> Unit
) {
    Column(modifier) {
        if (refreshing || items.isNotEmpty()) {
            Header(title = title, loading = refreshing, modifier = Modifier.fillMaxWidth()) {
                TextButton(
                    onClick = onMoreClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.alignBy(FirstBaseline)
                ) {
                    Text(text = "More")
                }
            }

            if (items.isNotEmpty()) {
                CarouselInt(
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
internal fun <T : Multi> CarouselInt(
    items: List<T>,
    onItemClick: (T, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    val contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)

    LazyRow(
        state = lazyListState,
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        itemsIndexed(items = items) { index, item ->
            PosterCard(
                media = item,
                onClick = { onItemClick(item, index) },
                modifier = Modifier
                    .animateItemPlacement()
                    .fillParentMaxHeight()
                    .aspectRatio(2 / 3f)
            )
        }
    }
}

@Composable
fun PosterCard(
    media: Multi,
    modifier: Modifier = Modifier,
    type: Multi.MediaType = media.mediaType ?: Multi.MediaType.MOVIE,
    onClick: (() -> Unit)? = null,
) {
    var title: String? = null
    var posterPath: String? = null
    when (type) {
        Multi.MediaType.MOVIE -> {
            media as Movie
            title = media.title
            posterPath = media.posterPath
        }
        Multi.MediaType.TV_SERIES -> {
            media as TV
            title = media.name
            posterPath = media.posterPath
        }
        else -> {}
    }
    Card(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
        ) {
            Text(
                text = title ?: "No title",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.Center)
            )

            if (posterPath != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(LocalTMDbBaseUrl.current + LocalPosterSize.current + posterPath).crossfade(true).build(),
                    contentDescription = title,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun Header(
    title: String,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    content: @Composable RowScope.() -> Unit = {}
) {
    Row(modifier) {
        Spacer(Modifier.width(16.dp))

        Text(
            text = title,
            color = contentColorFor(MaterialTheme.colorScheme.surface),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(vertical = 8.dp)
        )

        Spacer(Modifier.weight(1f))

        AnimatedVisibility(visible = loading) {
            AutoSizedCircularProgressIndicator(
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .padding(8.dp)
                    .size(16.dp)
            )
        }

        content()

        Spacer(Modifier.width(16.dp))
    }
}
