/*
 * Copyright (C) 2020-2021 Sandip Vaghela
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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun FABAdd(modifier: Modifier = Modifier, onClick: () -> Unit) {
  CommonFAB(
    icon = Icons.Rounded.Add,
    modifier = modifier,
    onClick = onClick,
  )
}

@Composable
fun FABDone(modifier: Modifier = Modifier, onClick: () -> Unit) {
  CommonFAB(
    icon = Icons.Rounded.Done,
    modifier = modifier,
    onClick = onClick,
  )
}

@Composable
fun FABSave(modifier: Modifier = Modifier, onClick: () -> Unit) {
  CommonFAB(
    icon = Icons.Rounded.Save,
    modifier = modifier,
    onClick = onClick,
  )
}

@Composable
fun FABEdit(modifier: Modifier = Modifier, onClick: () -> Unit) {
  CommonFAB(
    icon = Icons.Rounded.Edit,
    modifier = modifier,
    onClick = onClick,
  )
}

@Composable
internal fun CommonFAB(icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
  FloatingActionButton(
    onClick = onClick,
    modifier = modifier,
  ) {
    Icon(imageVector = icon, contentDescription = icon.name)
  }
}

val FABSize = 56.dp
