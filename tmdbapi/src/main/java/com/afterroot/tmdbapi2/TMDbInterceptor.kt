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

class TMDbInterceptor(val key: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val url = chain.request().url
            .newBuilder().addQueryParameter("api_key", key).build()
        val request = chain.request().newBuilder().url(url).build()
        return chain.proceed(request)
    }
}