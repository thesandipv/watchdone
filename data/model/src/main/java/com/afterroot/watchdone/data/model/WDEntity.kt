/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.model

interface WDEntity {
  val id: Long
}

interface TmdbIdEntity {
  val tmdbId: Int?
}

interface Watchable {
  val isWatched: Boolean?
}
