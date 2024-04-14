/*
 * Copyright (C) 2020-2024 Sandip Vaghela
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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.afterroot.ui.common.compose.components.DynamicChipGroup
import com.afterroot.ui.common.compose.components.SelectionType
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.resources.R

@Composable
fun MediaTypeFilter(
  modifier: Modifier = Modifier,
  preSelect: MediaType? = null,
  showOnlySelected: Boolean = false,
  onSelectionCleared: () -> Unit = {},
  onMediaTypeSelected: (selectedMediaType: MediaType) -> Unit,
) {
  MediaTypeFilter(modifier, preSelect, showOnlySelected) { index, _, selectedList ->
    if (selectedList.isNotEmpty()) {
      if (index == 0) { // Movie
        onMediaTypeSelected(MediaType.MOVIE)
      } else { // TV
        onMediaTypeSelected(MediaType.SHOW)
      }
    } else {
      onSelectionCleared()
    }
  }
}

@Composable
fun MediaTypeFilter(
  modifier: Modifier = Modifier,
  preSelect: MediaType? = null,
  showOnlySelected: Boolean = false,
  onSelectionChanged: (index: Int, title: String, selectedList: List<Int>) -> Unit,
) {
  val movies = stringResource(id = R.string.text_search_movies)
  val shows = stringResource(id = R.string.text_search_show)
  DynamicChipGroup(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    list = listOf(movies, shows),
    icons = listOf(Icons.Outlined.Movie, Icons.Outlined.Tv),
    preSelectItem = when (preSelect) {
      MediaType.MOVIE -> movies
      MediaType.SHOW -> shows
      else -> null
    },
    onSelectedChanged = { index, title, _, _, selectedList ->
      onSelectionChanged(index, title, selectedList)
    },
    showOnlySelected = showOnlySelected,
    selectionType = SelectionType.Single,
  ) { _, title, icon, selected, onClick, clear ->
    FilterChip(
      selected = selected,
      onClick = { onClick(selected) },
      label = { Text(text = title) },
      leadingIcon = {
        if (icon != null) {
          Icon(
            imageVector = icon,
            contentDescription = "$title Icon",
            modifier = Modifier.size(FilterChipDefaults.IconSize),
            tint = MaterialTheme.colorScheme.onSurface,
          )
        }
      },
      trailingIcon = {
        if (selected && showOnlySelected) {
          IconButton(modifier = Modifier.size(InputChipDefaults.IconSize), onClick = {
            clear()
          }) {
            Icon(
              imageVector = Icons.Rounded.Clear,
              contentDescription = stringResource(
                id = R.string.content_desc_clear_filter,
              ),
              modifier = Modifier.size(FilterChipDefaults.IconSize),
              tint = MaterialTheme.colorScheme.onSurface,
            )
          }
        }
      },
    )
  }
}
