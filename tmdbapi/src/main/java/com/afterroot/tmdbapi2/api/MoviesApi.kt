/*
 * Copyright (C) 2020 Sandip Vaghela
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

package com.afterroot.tmdbapi2.api

import com.afterroot.tmdbapi.model.MovieDb
import com.afterroot.tmdbapi.model.core.MovieResultsPage
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MoviesApi {
    @GET("3/trending/movie/{by}")
    suspend fun getMoviesTrendingInSearch(@Path("by") by: String): MovieResultsPage

    @GET("3/movie/{movie_id}")
    suspend fun getMovieInfo(@Path("movie_id") movieId: Int): MovieDb

    @GET("3/movie/{movie_id}")
    suspend fun getFullMovieInfo(@Path("movie_id") movieId: Int, @Query("append_to_response") appendableResponses: String): MovieDb
}