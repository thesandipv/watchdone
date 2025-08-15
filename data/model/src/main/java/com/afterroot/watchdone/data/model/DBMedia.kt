/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class DBMedia(
  val id: Int = 0,
  val releaseDate: String? = null,
  val title: String? = null,
  @field:JvmField
  val isWatched: Boolean = false,
  val posterPath: String? = null,
  @ServerTimestamp
  val timestamp: Timestamp = Timestamp.now(),
  val mediaType: MediaType? = null,
  val rating: Float? = null,
  val watched: List<String> = emptyList(),
) {
  companion object {
    val Empty = DBMedia()
  }
}
