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

package com.afterroot.tmdbapi

import com.fasterxml.jackson.annotation.JsonProperty
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.GET

const val TMDB_BASE_URL = "https://api.themoviedb.org/"

interface Auth {
    @GET("3/authentication/token/new")
    fun getRequestToken(): Call<RequestToken>
}

data class RequestToken(
    @JsonProperty("success") val isSuccess: Boolean,
    @JsonProperty("expires_at") val expireAt: String,
    @JsonProperty("request_token") val token: String
)

class TMDbInterceptor(val key: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url
            .newBuilder().addQueryParameter("api_key", key).build()
        val request = chain.request().newBuilder().url(url).build()
        return chain.proceed(request)
    }
}

class Api(apiKey: String) {
    private val client = OkHttpClient().newBuilder()
        .addInterceptor(TMDbInterceptor(apiKey))
        .addInterceptor(HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }).build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(TMDB_BASE_URL)
        .addConverterFactory(JacksonConverterFactory.create())
        .client(client)
        .build()
}

fun TmdbApi.imageUrl(pathString: String, size: String): String =
    configuration.secureBaseUrl.toHttpUrl().newBuilder().addPathSegment(size).addPathSegment(pathString).build()
        .toUrl().toString()
