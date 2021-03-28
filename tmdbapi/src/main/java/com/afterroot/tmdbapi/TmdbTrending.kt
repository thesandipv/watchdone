/*
 * Copyright (C) 2020-2020 Sandip Vaghela
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
package com.afterroot.tmdbapi

import com.afterroot.tmdbapi.model.core.MovieResultsPage
import com.afterroot.tmdbapi.tools.ApiUrl

class TmdbTrending(api: TmdbApi) : AbstractTmdbApi(api) {

    fun getMovies(by: String = BY_DAY): MovieResultsPage {
        val apiUrl = ApiUrl(TMDB_METHOD_TRENDING, TmdbMovies.TMDB_METHOD_MOVIE, by)
        return mapJsonResult(apiUrl, MovieResultsPage::class.java)
    }

    companion object {
        const val TMDB_METHOD_TRENDING = "trending"
        const val BY_DAY = "day"
        const val BY_WEEK = "week"
    }
}
