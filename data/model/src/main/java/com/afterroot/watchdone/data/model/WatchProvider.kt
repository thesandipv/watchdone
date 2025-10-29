/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.model

fun WatchProviderResult.getProvidersForCountry(country: String) = results.getOrDefault(
  country,
  null,
)

data class WatchProvider(
  val displayPriority: Int?,
  val logoPath: String,
  val providerId: Int,
  val providerName: String,
)

data class WatchProviders(
  val link: String,
  val flatrate: List<WatchProvider> = emptyList(),
  val buy: List<WatchProvider> = emptyList(),
  val rent: List<WatchProvider> = emptyList(),
  val free: List<WatchProvider> = emptyList(),
)

data class WatchProviderResult(val id: Int? = null, val results: Map<String, WatchProviders>)
