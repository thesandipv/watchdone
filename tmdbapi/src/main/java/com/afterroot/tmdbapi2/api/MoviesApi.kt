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
package com.afterroot.tmdbapi2.api

import com.afterroot.tmdbapi.model.Credits
import com.afterroot.tmdbapi.model.MovieImages
import com.afterroot.tmdbapi.model.MovieList
import com.afterroot.tmdbapi.model.NetworkMovie
import com.afterroot.tmdbapi.model.Video
import com.afterroot.tmdbapi.model.core.MovieKeywords
import com.afterroot.tmdbapi.model.core.MovieResultsPage
import com.afterroot.tmdbapi.model.core.ResultsPage
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MoviesApi {
    @GET("3/trending/movie/{by}")
    suspend fun getMoviesTrendingInSearch(@Path("by") by: String): MovieResultsPage

    @GET("3/movie/{movie_id}")
    suspend fun getMovieInfo(@Path("movie_id") movieId: Int): NetworkMovie

    @GET("3/movie/{movie_id}")
    suspend fun getFullMovieInfo(
        @Path("movie_id") movieId: Int,
        @Query("append_to_response") appendableResponses: String
    ): NetworkMovie

    @GET("3/movie/popular")
    suspend fun getPopular(@Query("region") region: String?): MovieResultsPage

    @GET("3/movie/latest")
    suspend fun getLatest(@Query("region") region: String?): NetworkMovie

    @GET("3/movie/now_playing")
    suspend fun getNowPlaying(@Query("region") region: String?): MovieResultsPage

    @GET("3/movie/top_rated")
    suspend fun getTopRated(@Query("region") region: String?): MovieResultsPage

    @GET("3/movie/upcoming")
    suspend fun getUpcoming(@Query("region") region: String?): MovieResultsPage

    @GET("3/movie/{movie_id}/similar")
    suspend fun getSimilar(@Path("movie_id") movieId: Int): MovieResultsPage

    @GET("3/movie/{movie_id}/recommendations")
    suspend fun getRecommended(@Path("movie_id") movieId: Int): MovieResultsPage

    @GET("3/movie/{movie_id}/credits")
    suspend fun getCredits(@Path("movie_id") movieId: Int): Credits

    @GET("3/movie/{movie_id}/images")
    suspend fun getImages(@Path("movie_id") movieId: Int): MovieImages

    @GET("3/movie/{movie_id}/videos")
    suspend fun getVideos(@Path("movie_id") movieId: Int): Video.Results

    @GET("3/movie/{movie_id}/keywords")
    suspend fun getKeywords(@Path("movie_id") movieId: Int): MovieKeywords

    @GET("3/movie/{movie_id}/lists")
    suspend fun getLists(@Path("movie_id") movieId: Int): ResultsPage<MovieList>
}
