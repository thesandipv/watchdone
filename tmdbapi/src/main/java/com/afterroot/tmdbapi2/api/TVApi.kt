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

import com.afterroot.tmdbapi.TvResultsPage
import com.afterroot.tmdbapi.model.Credits
import com.afterroot.tmdbapi.model.tv.TvSeries
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TVApi {
    @GET("3/tv/{id}")
    suspend fun getTVInfo(@Path("id") id: Int): TvSeries

    @GET("3/tv/{id}")
    suspend fun getFullTvInfo(
        @Path("id") id: Int,
        @Query("append_to_response") appendableResponses: String
    ): TvSeries

    @GET("3/tv/{id}/credits")
    suspend fun getCredits(@Path("id") id: Int): Credits

    @GET("3/tv/{id}/similar")
    suspend fun getSimilar(@Path("id") tvId: Int): TvResultsPage
}
