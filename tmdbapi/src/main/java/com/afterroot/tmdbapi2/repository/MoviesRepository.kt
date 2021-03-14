/*
 * Copyright (C) 2020-2021 Sandip Vaghela
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

package com.afterroot.tmdbapi2.repository

import com.afterroot.tmdbapi.TmdbTrending
import com.afterroot.tmdbapi.model.Credits
import com.afterroot.tmdbapi.model.MovieDb
import com.afterroot.tmdbapi.model.MovieImages
import com.afterroot.tmdbapi.model.MovieList
import com.afterroot.tmdbapi.model.Video
import com.afterroot.tmdbapi.model.core.MovieKeywords
import com.afterroot.tmdbapi.model.core.ResultsPage
import com.afterroot.tmdbapi2.api.MoviesApi
import com.afterroot.tmdbapi2.model.MovieAppendableResponses

class MoviesRepository(val api: MoviesApi) {
    suspend fun getMoviesTrendingInSearch(by: String = TmdbTrending.BY_DAY) = api.getMoviesTrendingInSearch(by)

    suspend fun getMovieInfo(movieId: Int) = api.getMovieInfo(movieId)

    suspend fun getFullMovieInfo(movieId: Int, vararg appendableResponses: MovieAppendableResponses): MovieDb {
        val joined = appendableResponses.joinToString(",")
        return api.getFullMovieInfo(movieId, joined)
    }

    suspend fun getPopular(region: String? = null) = api.getPopular(region)
    suspend fun getLatest(region: String? = null) = api.getLatest(region)
    suspend fun getNowPlaying(region: String? = null) = api.getNowPlaying(region)
    suspend fun getTopRated(region: String? = null) = api.getTopRated(region)
    suspend fun getUpcoming(region: String? = null) = api.getUpcoming(region)
    suspend fun getSimilar(movieId: Int) = api.getSimilar(movieId)
    suspend fun getRecommended(movieId: Int) = api.getRecommended(movieId)
    suspend fun getCredits(movieId: Int): Credits = api.getCredits(movieId)
    suspend fun getImages(movieId: Int): MovieImages = api.getImages(movieId)
    suspend fun getVideos(movieId: Int): Video.Results = api.getVideos(movieId)
    suspend fun getKeywords(movieId: Int): MovieKeywords = api.getKeywords(movieId)
    suspend fun getLists(movieId: Int): ResultsPage<MovieList> = api.getLists(movieId)
}