/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.mapper

import app.moviebase.tmdb.model.TmdbConfigurationCountry
import com.afterroot.watchdone.data.model.Country

fun TmdbConfigurationCountry.toCountry(): Country = Country(
  iso = iso3166,
  englishName = englishName,
  nativeName = nativeName,
)
