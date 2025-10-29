/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.media

import com.afterroot.watchdone.data.model.Media

interface MediaDataSource {
  suspend fun getMedia(media: Media): Media
}
