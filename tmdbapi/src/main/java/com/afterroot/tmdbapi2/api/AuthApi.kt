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

import com.afterroot.tmdbapi2.model.RequestBodyToken
import com.afterroot.tmdbapi2.model.RequestToken
import com.afterroot.tmdbapi2.model.ResponseAccessToken
import com.afterroot.tmdbapi2.model.ResponseRequestToken
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {
    @GET("3/authentication/token/new")
    suspend fun getRequestToken(): RequestToken

    @POST("4/auth/request_token")
    suspend fun createRequestToken(
        @Body requestBodyToken: RequestBodyToken
    ): ResponseRequestToken

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("4/auth/access_token")
    suspend fun createAccessToken(
        @Header("Authorization") apiAccessToken: String,
        @Body requestBodyToken: RequestBodyToken
    ): ResponseAccessToken
}