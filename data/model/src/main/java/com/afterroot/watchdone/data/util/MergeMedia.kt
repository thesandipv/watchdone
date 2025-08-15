/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.util

import com.afterroot.watchdone.data.model.Media

fun mergeMedia(local: Media = Media.EMPTY, tmdb: Media = Media.EMPTY) = local.copy(
  tmdbId = tmdb.tmdbId ?: local.tmdbId,
  releaseDate = tmdb.releaseDate ?: local.releaseDate,
  title = tmdb.title ?: local.title,
  isWatched = tmdb.isWatched ?: local.isWatched,
  posterPath = tmdb.posterPath ?: local.posterPath,
  mediaType = tmdb.mediaType ?: local.mediaType,
  rating = tmdb.rating ?: local.rating,
)
