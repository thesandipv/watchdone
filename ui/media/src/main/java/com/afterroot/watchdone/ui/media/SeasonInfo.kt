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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.tivi.common.compose.Layout
import com.afterroot.ui.common.compose.components.FilterChipGroup
import com.afterroot.ui.common.compose.theme.ubuntuTypography
import com.afterroot.watchdone.data.model.Episode
import com.afterroot.watchdone.data.model.Season
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.utils.State

@Composable
fun Seasons(
    tv: TV,
    season: State<Season>,
    watchedEpisodes: Map<String, Boolean>,
    onSeasonSelected: (Int) -> Unit,
    onWatchClicked: (episode: Episode, isWatched: Boolean) -> Unit
) {
    ProvideTextStyle(value = ubuntuTypography.titleMedium) {
        Text(
            text = "Seasons",
            modifier = Modifier.padding(horizontal = Layout.bodyMargin, vertical = Layout.gutter)
        )
    }

    SeasonsChips(tv = tv, onSeasonSelected = onSeasonSelected)
    SeasonsDetail(season = season, watchedEpisodes = watchedEpisodes, onWatchClicked = onWatchClicked)
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
fun SeasonsDetail(
    season: State<Season>,
    watchedEpisodes: Map<String, Boolean>,
    onWatchClicked: (episode: Episode, isWatched: Boolean) -> Unit
) {
    val bodyMargin = Layout.bodyMargin
    val gutter = Layout.gutter

    Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.animateContentSize()) {
        when (season) {
            is State.Success -> {
                season.data.overview?.let {
                    if (it.isNotBlank()) {
                        OverviewText(
                            text = it,
                            modifier = Modifier.padding(horizontal = bodyMargin, vertical = gutter)
                        )
                    }
                }

                season.data.episodes?.let { episodes ->
                    Text(
                        text = "${episodes.count()} Episodes",
                        modifier = Modifier.padding(horizontal = bodyMargin, vertical = gutter)
                    )
                    episodes.forEach { episode ->
                        val watched = if (watchedEpisodes.containsKey(episode.id.toString())) {
                            watchedEpisodes[episode.id.toString()] ?: false
                        } else {
                            false
                        }

                        EpisodeItem(episode = episode, isWatched = watched, onWatchClicked = onWatchClicked)
                    }
                }
            }
            is State.Failed -> {
                Text(text = "Error while loading", modifier = Modifier.padding(horizontal = bodyMargin, vertical = gutter))
            }
            is State.Loading -> {
                EpisodeItemPlaceholder()
            }
        }
    }
}

@Composable
fun EpisodeItem(
    episode: Episode,
    isWatched: Boolean = false,
    onWatchClicked: (episode: Episode, isWatched: Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Layout.bodyMargin, vertical = Layout.gutter),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            ProvideTextStyle(value = ubuntuTypography.bodyMedium) {
                episode.name?.let { Text(text = it) }
                episode.airDate?.let { Text(text = it) }
            }
        }

        FilledTonalIconToggleButton(
            checked = isWatched,
            onCheckedChange = {
                onWatchClicked(episode, it)
            }
        ) {
            if (isWatched) {
                Icon(imageVector = Icons.Rounded.Clear, contentDescription = "Remove Episode from Watched")
            } else {
                Icon(imageVector = Icons.Rounded.Done, contentDescription = "Add Episode from Watched")
            }
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
