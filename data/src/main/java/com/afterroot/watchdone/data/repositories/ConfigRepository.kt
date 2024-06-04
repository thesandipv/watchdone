/*
 * Copyright (C) 2020-2022 AfterROOT
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
package com.afterroot.watchdone.data.repositories

import app.moviebase.tmdb.Tmdb3
import javax.inject.Inject

class ConfigRepository @Inject constructor(
  private val tmdb: Tmdb3,
) {
  suspend fun getConfig() = tmdb.configuration.getApiConfiguration()
  suspend fun getCountries() = tmdb.configuration.getCountries()
}
