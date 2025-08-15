/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.search

import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.model.MediaType

interface SearchDataSource {
  suspend fun search(params: Params): List<Media>

  data class Params(val mediaType: MediaType, val query: String, val page: Int = 1)
}
