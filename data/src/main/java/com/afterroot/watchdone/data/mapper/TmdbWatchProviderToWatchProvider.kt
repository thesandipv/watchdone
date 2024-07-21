/*
 * Copyright (C) 2020-2024 Sandip Vaghela
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
