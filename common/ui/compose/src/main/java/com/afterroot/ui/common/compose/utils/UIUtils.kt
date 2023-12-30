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
package com.afterroot.ui.common.compose.utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.afterroot.watchdone.data.model.DarkThemeConfig
import com.afterroot.watchdone.data.model.UserData
import com.afterroot.watchdone.utils.State

@Composable
fun CenteredRow(modifier: Modifier = Modifier, content: @Composable (RowScope) -> Unit) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        content(this)
    }
}

fun Modifier.bottomNavigationPadding() = this.padding(bottom = 56.dp)

fun Modifier.sidePadding(padding: Dp = 16.dp, applyBottom: Boolean = false) =
    padding(
        start = padding,
        top = padding,
        end = padding,
        bottom = if (applyBottom) padding else 0.dp,
    )

val TopBarWindowInsets = WindowInsets(top = 0)

@Composable
fun shouldUseDarkTheme(
    uiState: State<UserData>,
): Boolean = when (uiState) {
    is State.Loading -> isSystemInDarkTheme()
    is State.Success -> when (uiState.data.darkThemeConfig) {
        DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.DARK -> true
    }

    else -> false
}

@Composable
fun shouldDisableDynamicTheming(
    uiState: State<UserData>,
): Boolean = when (uiState) {
    is State.Success -> !uiState.data.useDynamicColor
    else -> false
}

/**
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)
