/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.media.recommended

import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.model.MediaType

interface RecommendedDataSource {
  suspend operator fun invoke(mediaId: Int, mediaType: MediaType, page: Int): List<Media>
}
