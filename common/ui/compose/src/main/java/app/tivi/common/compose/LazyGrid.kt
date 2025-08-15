/*
 * Copyright 2023, Google LLC, Christopher Banes and the Tivi project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package app.tivi.common.compose

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.runtime.Composable

fun LazyGridScope.fullSpanItem(
  key: Any? = null,
  contentType: Any? = null,
  content: @Composable LazyGridItemScope.() -> Unit,
) {
  item(
    key = key,
    span = { GridItemSpan(maxLineSpan) },
    contentType = contentType,
    content = content,
  )
}
