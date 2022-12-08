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

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
    onSelectionChanged: (label: String, selected: Boolean) -> Unit = { _, _ -> }
) {
    FilterChip(
        modifier = modifier,
        selected = selected,
        onClick = {
            onSelectionChanged(text ?: "", !selected)
        },
        label = {
            Text(text = text ?: "")
        },
        leadingIcon = leadingIcon
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
    selectionType: SelectionType = SelectionType.Single,
    onSelectedChanged: (selected: String, selectedChips: List<String>) -> Unit
) {
    val selectedChips = remember { mutableStateListOf<String>() }

    if (preSelect.isNotEmpty() && selectedChips.isEmpty()) {
        selectedChips.addAll(preSelect)
    }

    Column(horizontalAlignment = Alignment.Start, modifier = modifier) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.SpaceEvenly
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
                        if (icons[index] != null) {
                            icons[index]?.let { icon ->
                                Icon(
                                    imageVector = icon,
                                    contentDescription = "$it Icon",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
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

                        onSelectedChanged(label, selectedChips)
                    }
                )
            }
        }
    }
}

enum class SelectionType {
    Single, Multiple
}
