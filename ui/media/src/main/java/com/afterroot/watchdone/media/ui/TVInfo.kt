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

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.afterroot.ui.common.compose.components.FilterChipGroup
import com.afterroot.watchdone.data.model.TV

@Composable
fun TVCompose(tv: TV) {
    SeasonsChips(seasonsCount = tv.numberOfSeasons) {
    }
}

@Composable
fun SeasonsChips(seasonsCount: Int, onSeasonSelected: (Int) -> Unit) {
    val seasonsList = mutableListOf<String>()
    repeat(seasonsCount) {
        seasonsList.add(it, "Season ${it + 1}")
    }

    FilterChipGroup(
        horizontalPadding = 16.dp,
        chipSpacing = 8.dp,
        list = seasonsList,
        onSelectedChanged = { selected, _ ->
            onSeasonSelected(seasonsList.indexOf(selected) + 1)
        }
    )
}

@Preview
@Composable
fun PreviewSeasonChips() {
    SeasonsChips(seasonsCount = 6) {
    }
}
