/*
 * Copyright (C) 2020-2023 Sandip Vaghela
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

data class WatchProviderResult(
  val id: Int? = null,
  val results: Map<String, WatchProviders>,
)
