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
package com.afterroot.watchdone.ui.discover

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.afterroot.ui.common.compose.components.FilterChipGroup
import com.afterroot.watchdone.viewmodel.DiscoverActions
import com.afterroot.watchdone.viewmodel.DiscoverViewModel
import info.movito.themoviedbapi.model.Multi
import com.afterroot.watchdone.resources.R as CommonR

@Composable
fun DiscoverChips(discoverViewModel: DiscoverViewModel) {
    FilterChipGroup(
        modifier = Modifier.padding(vertical = 8.dp),
        chipSpacing = 12.dp,
        horizontalPadding = dimensionResource(id = CommonR.dimen.padding_horizontal),
        icons = listOf(Icons.Outlined.Movie, Icons.Outlined.Tv),
        list = listOf("Movies", "TV"),
        preSelect = listOf("Movies")
    ) { selected, _ ->
        when (selected) {
            "Movies" -> {
                discoverViewModel.submitAction(DiscoverActions.SetMediaType(Multi.MediaType.MOVIE))
            }
            "TV" -> {
                discoverViewModel.submitAction(DiscoverActions.SetMediaType(Multi.MediaType.TV_SERIES))
            }
        }
    }
}
