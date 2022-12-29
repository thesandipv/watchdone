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
package com.afterroot.watchdone.ui.media

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.afterroot.data.utils.valueOrBlank
import com.afterroot.ui.common.compose.components.BasePosterCard
import com.afterroot.ui.common.compose.components.FilterChipGroup
import com.afterroot.ui.common.compose.components.Header
import com.afterroot.ui.common.compose.theme.ubuntuTypography
import com.afterroot.watchdone.data.model.Episode
import com.afterroot.watchdone.data.model.Season
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.utils.State
import com.afterroot.watchdone.viewmodel.MediaInfoViewModel
import com.afterroot.watchdone.viewmodel.SelectedMedia
import info.movito.themoviedbapi.model.Multi
import info.movito.themoviedbapi.model.people.Person
import info.movito.themoviedbapi.model.people.PersonCast
import info.movito.themoviedbapi.model.people.PersonCrew
import timber.log.Timber

@Composable
fun Seasons(viewModel: MediaInfoViewModel = hiltViewModel()) {
    val viewState by viewModel.state.collectAsState()
    Timber.d("Seasons: $viewState")
    if (viewState.mediaType == Multi.MediaType.TV_SERIES) {
        val tv = (viewState.selectedMedia as SelectedMedia.TV).data
        Seasons(tv = tv, season = viewState.seasonInfo, onSeasonSelected = viewModel::selectSeason, onWatchClicked = {
            viewModel.markEpisode(it.id, true)
        })
    }
}

@Composable
internal fun Seasons(tv: TV, season: State<Season>, onSeasonSelected: (Int) -> Unit, onWatchClicked: (Episode) -> Unit) {
    ProvideTextStyle(value = ubuntuTypography.titleMedium) {
        Text(
            text = "Seasons",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
    }

    SeasonsChips(tv = tv, onSeasonSelected = onSeasonSelected)
    SeasonsDetail(season = season, onWatchClicked = onWatchClicked)
}

@Composable
fun SeasonsChips(tv: TV, onSeasonSelected: (Int) -> Unit) {
    val seasonsList = tv.seasons?.mapIndexed { index, season ->
        season.name ?: "Season ${index + 1}"
    }

    FilterChipGroup(
        horizontalPadding = 16.dp,
        chipSpacing = 8.dp,
        list = seasonsList ?: manufactureSeasonList(tv.numberOfSeasons),
        onSelectedChangedIndexed = { index, _, _ ->
            onSeasonSelected(index + 1)
        },
        preSelectItem = seasonsList?.first()
    )
}

fun manufactureSeasonList(numberOfSeasons: Int): List<String> = mutableListOf<String>().apply {
    repeat(numberOfSeasons) {
        this.add(it, "Season ${it + 1}")
    }
}

@Composable
fun SeasonsDetail(season: State<Season>, onWatchClicked: (Episode) -> Unit) {
    Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.animateContentSize()) {
        when (season) {
            is State.Success -> {
                OverviewText(
                    text = season.data.overview.valueOrBlank(),
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .padding(bottom = 4.dp)
                )

                Text(
                    text = "${season.data.episodes?.count()} Episodes",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                season.data.episodes?.forEach {
                    EpisodeItem(episode = it, onWatchClicked = onWatchClicked)
                }
            }
            is State.Failed -> {
                Text(text = "Error while loading", modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
            }
            is State.Loading -> {
                EpisodeItemPlaceholder()
            }
        }
    }
}

@Composable
fun EpisodeItem(episode: Episode, onWatchClicked: (Episode) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            ProvideTextStyle(value = ubuntuTypography.bodyMedium) {
                episode.name?.let { Text(text = it) }
                episode.airDate?.let { Text(text = it) }
            }
        }
        Button(onClick = {
            onWatchClicked(episode)
        }) {
            Text(text = "Add")
        }
    }
}

@Composable
fun EpisodeItemPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height((32 * 8).dp)
    ) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }
}

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
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.alignBy(FirstBaseline)
                ) {
                    Text(text = "More")
                }
            }
        }
        if (items.isNotEmpty()) {
            PersonRow(items = items, onItemClick = { index, item ->
                Timber.d("PersonRow: Clicked: Index $index Item: $item")
            }, modifier = Modifier.fillMaxHeight())
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
            key = { index, item ->
                item.id
            },
            itemContent = { index, item ->
                PersonItem(item = item, modifier = Modifier.fillParentMaxHeight(), onClick = {
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
                .height(192.dp)
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
