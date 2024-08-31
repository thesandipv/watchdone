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
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.afterroot.data.utils.valueOrBlank

/**
 * Chip of [String]
 * @param text text to display
 * @param selected Show as selected or not
 * @param onSelectionChanged callback to be invoked when a chip is clicked.
 * The lambda carries out [String], of which state is changed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonFilterChip(
  modifier: Modifier = Modifier,
  text: String? = null,
  leadingIcon: (@Composable () -> Unit)? = null,
  selected: Boolean = false,
  onSelectionChanged: (label: String, selected: Boolean) -> Unit = { _, _ -> },
) {
  FilterChip(
    modifier = modifier,
    selected = selected,
    onClick = {
      onSelectionChanged(text.valueOrBlank(), !selected)
    },
    label = {
      Text(text = text.valueOrBlank())
    },
    leadingIcon = leadingIcon,
  )
}

@Composable
fun AssistChip(
  modifier: Modifier = Modifier,
  text: String? = null,
  leadingIcon: (@Composable () -> Unit)? = null,
  onClick: () -> Unit,
) {
  AssistChip(
    modifier = modifier,
    onClick = onClick,
    label = {
      Text(text = text.valueOrBlank())
    },
    leadingIcon = leadingIcon,
  )
}

/**
 * Toggleable ChipGroup of String.
 * @param modifier the modifier to apply to this layout
 * @param list the list of String to display
 * @param chipSpacing Spacing between chips
 * @param horizontalPadding Spacing on both side of ChipGroup
 * @param onSelectedChanged callback to be invoked when a chip is clicked.
 * The lambda carries out two parameters,
 * 1. selected - the [String] that state changed
 * 2. selectedChips - List of Selected [String] objects
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipGroup(
  modifier: Modifier = Modifier,
  chipModifier: Modifier = Modifier,
  chipSpacing: Dp = 0.dp,
  horizontalPadding: Dp = 0.dp,
  icons: List<ImageVector?> = emptyList(),
  list: List<String>? = null,
  preSelect: List<String> = emptyList(),
  preSelectItem: String? = null,
  selectionType: SelectionType = SelectionType.Single,
  onSelectedChanged: ((selected: String, selectedChips: List<String>) -> Unit)? = null,
  onSelectedChangedIndexed: (
    (
      index: Int,
      selected: String,
      selectedChips: List<String>,
    ) -> Unit
  )? = null,
) {
  val selectedChips = remember { mutableStateListOf<String>() }

  if (selectedChips.isEmpty()) {
    if (preSelect.isNotEmpty()) {
      selectedChips.addAll(preSelect)
    }
    preSelectItem?.let { selectedChips.add(it) }
  }

  Column(horizontalAlignment = Alignment.Start, modifier = modifier) {
    Row(
      modifier = Modifier
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = horizontalPadding),
      horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
      list?.forEachIndexed { index, it ->
        if (index != 0) {
          Spacer(modifier = Modifier.width(chipSpacing))
        }
        CommonFilterChip(
          modifier = chipModifier,
          text = it,
          selected = selectedChips.contains(it),
          leadingIcon = {
            if (icons.isNotEmpty()) {
              icons[index]?.let { icon ->
                Icon(
                  imageVector = icon,
                  contentDescription = "$it Icon",
                  modifier = Modifier.size(FilterChipDefaults.IconSize),
                )
              }
            }
          },
          onSelectionChanged = { label, _ ->

            when (selectionType) {
              SelectionType.Single -> {
                selectedChips.apply {
                  clear()
                  add(label)
                }
              }

              SelectionType.Multiple -> {
                if (selectedChips.contains(label)) {
                  selectedChips.remove(label)
                } else {
                  selectedChips.add(label)
                }
              }
            }

            onSelectedChanged?.invoke(label, selectedChips)
            onSelectedChangedIndexed?.invoke(index, label, selectedChips)
          },
        )
      }
    }
  }
}

enum class SelectionType {
  Single,
  Multiple,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionChipGroup(
  modifier: Modifier = Modifier,
  chipModifier: Modifier = Modifier,
  chipSpacing: Dp = 0.dp,
  horizontalPadding: Dp = 0.dp,
  icons: List<ImageVector?> = emptyList(),
  list: List<String>? = null,
  onClick: ((index: Int, label: String) -> Unit)? = null,
) {
  val scrollState = rememberScrollState()
  Row(
    horizontalArrangement = Arrangement.SpaceEvenly,
    modifier = modifier.horizontalScroll(scrollState),
  ) {
    Spacer(modifier = Modifier.width(horizontalPadding))
    list?.forEachIndexed { index, it ->
      if (index != 0) {
        Spacer(modifier = Modifier.width(chipSpacing))
      }
      SuggestionChip(
        modifier = chipModifier,
        onClick = {
          onClick?.invoke(index, it)
        },
        label = {
          Text(text = it.valueOrBlank())
        },
        icon = {
          if (icons.isNotEmpty()) {
            icons[index]?.let { icon ->
              Icon(
                imageVector = icon,
                contentDescription = "$it Icon",
                modifier = Modifier.size(FilterChipDefaults.IconSize),
              )
            }
          }
        },
      )
    }
    Spacer(modifier = Modifier.width(horizontalPadding))
  }
}

@Composable
fun DynamicChipGroup(
  modifier: Modifier = Modifier,
  horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
  icons: List<ImageVector?> = emptyList(),
  list: List<String>? = null,
  preSelect: List<String> = emptyList(),
  preSelectItem: String? = null,
  selectionType: SelectionType = SelectionType.Single,
  showOnlySelected: Boolean = false,
  onSelectedChanged: (
    (
      index: Int,
      title: String,
      selected: Boolean,
      list: List<String>,
      selectedList: List<Int>,
    ) -> Unit
  )? = null,
  chipContent: @Composable (
    index: Int,
    title: String,
    icon: ImageVector?,
    selected: Boolean,
    onClick: (selected: Boolean) -> Unit,
    clearSelection: () -> Unit,
  ) -> Unit,
) {
  val selectedList = remember { mutableStateListOf<Int>() }

  if (selectedList.isEmpty()) {
    if (list != null) {
      if (preSelect.isNotEmpty()) {
        selectedList.addAll(
          preSelect.map {
            list.indexOf(it)
          },
        )
      }
      preSelectItem?.let { selectedList.add(list.indexOf(preSelectItem)) }
    }
  }

  Row(modifier = modifier, horizontalArrangement = horizontalArrangement) {
    list?.forEachIndexed { index, label ->
      AnimatedVisibility(
        visible = if (showOnlySelected && selectedList.isNotEmpty() && selectedList[0] != -1) {
          selectedList.contains(index)
        } else {
          true
        },
        enter = fadeIn() + expandHorizontally(expandFrom = Alignment.CenterHorizontally),
        exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.CenterHorizontally),
      ) {
        chipContent(
          index,
          label,
          if (icons.isNotEmpty()) icons[index] else null,
          selectedList.contains(index),
          { selected ->
            when (selectionType) {
              SelectionType.Single -> {
                selectedList.apply {
                  clear()
                  add(index)
                }
              }

              SelectionType.Multiple -> {
                if (selectedList.contains(index)) {
                  selectedList.remove(index)
                } else {
                  selectedList.add(index)
                }
              }
            }

            onSelectedChanged?.invoke(index, label, selected, list, selectedList)
          },
          {
            selectedList.clear()
            onSelectedChanged?.invoke(index, label, false, list, selectedList)
          },
        )
      }
    }
  }
}
