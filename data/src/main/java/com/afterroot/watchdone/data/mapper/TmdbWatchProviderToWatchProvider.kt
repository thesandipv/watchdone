/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.mapper

import app.moviebase.tmdb.model.TmdbProvider
import app.moviebase.tmdb.model.TmdbProviders
import app.moviebase.tmdb.model.TmdbWatchProviderResult
import app.tivi.data.mappers.Mapper
import com.afterroot.watchdone.data.model.WatchProvider
import com.afterroot.watchdone.data.model.WatchProviderResult
import com.afterroot.watchdone.data.model.WatchProviders
import javax.inject.Inject

class TmdbProviderToWatchProvider @Inject constructor() : Mapper<TmdbProvider, WatchProvider> {
  override fun map(from: TmdbProvider): WatchProvider = WatchProvider(
    displayPriority = from.displayPriority,
    logoPath = from.logoPath,
    providerId = from.providerId,
    providerName = from.providerName,
  )
}

class TmdbProvidersToWatchProviders @Inject constructor(
  private val tmdbProviderToWatchProvider: TmdbProviderToWatchProvider,
) : Mapper<TmdbProviders, WatchProviders> {
  override fun map(from: TmdbProviders): WatchProviders = WatchProviders(
    link = from.link,
    flatrate = from.flatrate.map(tmdbProviderToWatchProvider::map),
    buy = from.buy.map(tmdbProviderToWatchProvider::map),
  )
}

class TmdbWatchProviderResultToWatchProviderResult @Inject constructor(
  private val tmdbProvidersToWatchProviders: TmdbProvidersToWatchProviders,
) : Mapper<TmdbWatchProviderResult, WatchProviderResult> {
  override fun map(from: TmdbWatchProviderResult): WatchProviderResult = WatchProviderResult(
    id = from.id,
    results = from.results.mapValues { tmdbProvidersToWatchProviders.map(it.value) },
  )
}
