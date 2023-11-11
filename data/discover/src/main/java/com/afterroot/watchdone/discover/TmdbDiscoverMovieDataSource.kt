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

package com.afterroot.watchdone.discover

import app.moviebase.tmdb.Tmdb3
import app.moviebase.tmdb.model.TmdbMovie
import com.afterroot.watchdone.data.mapper.toMedia
import com.afterroot.watchdone.data.model.Media
import javax.inject.Inject

class TmdbDiscoverMovieDataSource @Inject constructor(
    private val tmdb: Tmdb3,
) : DiscoverDataSource {
    override suspend fun invoke(page: Int, parameters: Map<String, Any?>): List<Media> {
        return tmdb.discover.discoverMovie(
            page,
            parameters = parameters,
        ).results.map(TmdbMovie::toMedia)
    }
}
