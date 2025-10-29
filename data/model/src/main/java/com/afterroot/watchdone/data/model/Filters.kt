/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.data.model

enum class WatchStateValues {
  WATCHED,
  PENDING,
  STARTED,
}

data class Filters(val watchState: WatchStateValues? = null, val mediaType: MediaType? = null) {
  companion object {
    val EMPTY = Filters()
  }
}
