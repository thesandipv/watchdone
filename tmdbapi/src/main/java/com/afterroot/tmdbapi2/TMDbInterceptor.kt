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

package com.afterroot.tmdbapi2

import okhttp3.Interceptor
import java.util.Locale

/**
 * OkHttp interceptor that adds Tmdb api key in each request
 */
class TMDbInterceptor(val key: String, val language: String = Locale.ENGLISH.toString(), val v4ApiKey: String? = null) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val req = chain.request()
        val url = req.url.newBuilder().addQueryParameter(Constants.PARAM_LANGUAGE, language)
            .addQueryParameter(Constants.PARAM_KEY, key)
        val request = req.newBuilder().url(url.build())
        if (v4ApiKey != null) {
            request.addHeader("authorization", v4ApiKey)
        }
        request.addHeader("content-type", "application/json;charset=utf-8")
        return chain.proceed(request.build())
    }
}