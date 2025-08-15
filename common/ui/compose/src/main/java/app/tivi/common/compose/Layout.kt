/*
 * Copyright 2023, Google LLC, Christopher Banes and the Tivi project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package app.tivi.common.compose

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.afterroot.ui.common.compose.components.LocalWindowSizeClass

object Layout {

  val bodyMargin: Dp
    @Composable get() = when (LocalWindowSizeClass.current.widthSizeClass) {
      WindowWidthSizeClass.Compact -> 16.dp
      WindowWidthSizeClass.Medium -> 32.dp
      else -> 64.dp
    }

  val gutter: Dp
    @Composable get() = when (LocalWindowSizeClass.current.widthSizeClass) {
      WindowWidthSizeClass.Compact -> 8.dp
      WindowWidthSizeClass.Medium -> 16.dp
      else -> 24.dp
    }

  val gridColumns: Int
    @Composable get() = when (LocalWindowSizeClass.current.widthSizeClass) {
      WindowWidthSizeClass.Compact -> 2
      WindowWidthSizeClass.Medium -> 3
      else -> 4
    }

  val profileGridColumns: Int
    @Composable get() = when (LocalWindowSizeClass.current.widthSizeClass) {
      WindowWidthSizeClass.Compact -> 3
      WindowWidthSizeClass.Medium -> 4
      else -> 5
    }

  val listColumns: Int
    @Composable get() = when (LocalWindowSizeClass.current.widthSizeClass) {
      WindowWidthSizeClass.Compact -> 1
      WindowWidthSizeClass.Medium -> 2
      else -> 2
    }
}

fun Modifier.bodyWidth() = fillMaxWidth()
  .composed {
    windowInsetsPadding(
      WindowInsets.systemBars
        .only(WindowInsetsSides.Horizontal),
    )
  }
