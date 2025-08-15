/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.mapper

import app.moviebase.tmdb.model.TmdbGenre
import com.afterroot.watchdone.data.model.Genre

fun TmdbGenre.toGenre() = Genre(
  id = id,
  name = name,
)
