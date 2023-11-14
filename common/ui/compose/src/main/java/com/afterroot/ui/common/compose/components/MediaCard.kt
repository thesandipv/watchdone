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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.tivi.common.compose.Layout
import app.tivi.common.compose.ui.AutoSizedCircularProgressIndicator
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV

@Composable
fun PosterCard(
    media: Media,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    BasePosterCard(
        title = media.title,
        posterPath = media.posterPath,
        modifier = modifier,
        onClick = onClick,
    )
}

@Composable
fun MovieCard(movie: Movie, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    BasePosterCard(
        title = movie.title,
        posterPath = movie.posterPath,
        modifier = modifier,
        onClick = onClick,
    )
}

@Composable
fun MediaCard(media: Media, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    BasePosterCard(
        title = media.title,
        posterPath = media.posterPath,
        modifier = modifier,
        onClick = onClick,
    )
}

@Composable
fun TVCard(tv: TV, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    BasePosterCard(
        title = tv.name,
        posterPath = tv.posterPath,
        modifier = modifier,
        onClick = onClick,
    )
}

@Composable
fun BasePosterCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    posterPath: String? = null,
    onClick: (() -> Unit)? = null,
) {
    Card(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        ) {
            Text(
                text = title ?: "",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.Center),
            )

            if (posterPath != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(
                            LocalTMDbBaseUrl.current + LocalPosterSize.current + posterPath,
                        ).crossfade(true).build(),
                    contentDescription = title,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}

@Composable
fun Backdrop(
    backdropPath: String?,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    title: String? = null,
    onClick: (() -> Unit)? = null,
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
            if (backdropPath != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(
                            LocalTMDbBaseUrl.current + LocalBackdropSize.current + backdropPath,
                        ).crossfade(true).build(),
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }

            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .padding(Layout.gutter * 2)
                        .align(Alignment.BottomStart),
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
    content: @Composable RowScope.() -> Unit = {},
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Spacer(Modifier.width(16.dp))

        Text(
            text = title,
            color = contentColorFor(MaterialTheme.colorScheme.surface),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(vertical = 8.dp),
        )

        Spacer(Modifier.weight(1f))

        AnimatedVisibility(visible = loading) {
            AutoSizedCircularProgressIndicator(
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .padding(8.dp)
                    .size(16.dp),
            )
        }

        content()

        Spacer(Modifier.width(16.dp))
    }
}
