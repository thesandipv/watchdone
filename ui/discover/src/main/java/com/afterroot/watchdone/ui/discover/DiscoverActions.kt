/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.ui.discover

import com.afterroot.watchdone.base.compose.Actions
import com.afterroot.watchdone.data.model.MediaType

sealed class DiscoverActions : Actions() {
  data class SetMediaType(val mediaType: MediaType) : DiscoverActions()
}
