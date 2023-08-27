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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.afterroot.ui.common.compose.components.BasePosterCard
import com.afterroot.ui.common.compose.components.Header
import com.afterroot.ui.common.compose.theme.ubuntuTypography
import info.movito.themoviedbapi.model.people.Person
import info.movito.themoviedbapi.model.people.PersonCast
import info.movito.themoviedbapi.model.people.PersonCrew
import timber.log.Timber

@Composable
fun <T : Person> PersonRow(
    items: List<T>,
    modifier: Modifier = Modifier,
    title: String,
    refreshing: Boolean = false,
    onMoreClick: (() -> Unit)? = null
) {
    Column(modifier) {
        Header(title = title, loading = refreshing, modifier = Modifier.fillMaxWidth()) {
            if (onMoreClick != null) {
                TextButton(
                    onClick = onMoreClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.alignBy(FirstBaseline)
                ) {
                    Text(text = "More")
                }
            }
        }
        if (items.isNotEmpty()) {
            PersonRow(items = items, onItemClick = { index, item ->
                Timber.d("PersonRow: Clicked: Index $index Item: $item")
            }, modifier = Modifier)
        }
    }
}

@Composable
private fun <T : Person> PersonRow(
    items: List<T>,
    onItemClick: (index: Int, item: T) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)

    LazyRow(
        state = listState,
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(
            items = items,
            key = { index, _ ->
                index
            },
            itemContent = { index, item ->
                PersonItem(item = item, onClick = {
                    onItemClick(index, item)
                })
            }
        )
    }
}

@Composable
fun <T : Person> PersonItem(
    item: T,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Column(modifier = modifier.width(128.dp)) {
        BasePosterCard(
            title = item.name,
            posterPath = item.profilePath,
            modifier = Modifier
                .aspectRatio(2 / 3f)
                .fillMaxWidth()
                .clickable {
                    onClick?.invoke()
                }
        )

        Spacer(modifier = Modifier.size(4.dp))

        ProvideTextStyle(value = ubuntuTypography.bodyMedium) {
            when (item) {
                is PersonCast -> {
                    item.character?.let { Text(text = it) }
                }
                is PersonCrew -> {
                    item.department?.let { Text(text = it) }
                }
            }
        }

        Spacer(modifier = Modifier.size(2.dp))

        ProvideTextStyle(
            value = ubuntuTypography.bodySmall.copy(
                color = LocalContentColor.current.copy(alpha = 0.8f),
                fontStyle = FontStyle.Italic
            )
        ) {
            item.name?.let { Text(text = it) }
        }
    }
}

@Preview
@Composable
fun PreviewPersonItem() {
    Column() {
        BasePosterCard(
            title = "Lorem",
            posterPath = "na",
            modifier = Modifier
                .height(192.dp)
                .aspectRatio(2 / 3f)
        )
        ProvideTextStyle(value = ubuntuTypography.bodyMedium) {
            Text(text = "First Last")
        }
    }
}
