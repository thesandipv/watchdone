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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.afterroot.ui.common.compose.components.FilterChipGroup
import com.afterroot.ui.common.compose.theme.ubuntuTypography
import com.afterroot.watchdone.data.model.Episode
import com.afterroot.watchdone.data.model.Season
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.utils.State
import com.afterroot.watchdone.viewmodel.MediaInfoViewModel
import com.afterroot.watchdone.viewmodel.SelectedMedia
import info.movito.themoviedbapi.model.Multi
import timber.log.Timber

@Composable
fun Seasons(viewModel: MediaInfoViewModel = hiltViewModel()) {
    val viewState by viewModel.state.collectAsState()
    Timber.d("Seasons: $viewState")
    if (viewState.mediaType == Multi.MediaType.TV_SERIES) {
        val tv = (viewState.selectedMedia as SelectedMedia.TV).data
        Seasons(tv = tv, season = viewState.seasonInfo, onSeasonSelected = viewModel::selectSeason)
    }
}

@Composable
internal fun Seasons(tv: TV, season: State<Season>, onSeasonSelected: (Int) -> Unit) {
    SeasonsChips(seasonsCount = tv.numberOfSeasons, onSeasonSelected = onSeasonSelected)
    SeasonsDetail(season = season)
}

@Composable
fun SeasonsChips(seasonsCount: Int, onSeasonSelected: (Int) -> Unit) {
    val seasonsList = mutableListOf<String>()
    repeat(seasonsCount) {
        seasonsList.add(it, "Season ${it + 1}")
    }

    onSeasonSelected(seasonsList.indexOf(seasonsList.first()) + 1)

    FilterChipGroup(
        horizontalPadding = 16.dp,
        chipSpacing = 8.dp,
        list = seasonsList,
        onSelectedChanged = { selected, _ ->
            onSeasonSelected(seasonsList.indexOf(selected) + 1)
        },
        preSelectItem = seasonsList.first()
    )
}

@Preview
@Composable
fun PreviewSeasonChips() {
    SeasonsChips(seasonsCount = 6) {
    }
}

@Composable
fun SeasonsDetail(season: State<Season>) {
    Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.animateContentSize()) {
        when (season) {
            is State.Success -> {
                Text(
                    text = "${season.data.episodes?.count()} Episodes",
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 4.dp)
                )
                season.data.episodes?.forEach {
                    EpisodeItem(episode = it)
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
fun EpisodeItem(episode: Episode) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        ProvideTextStyle(value = ubuntuTypography.bodyMedium) {
            episode.name?.let { Text(text = it) }
            episode.airDate?.let { Text(text = it) }
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
