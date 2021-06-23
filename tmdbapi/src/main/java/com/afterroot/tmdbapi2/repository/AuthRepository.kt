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

import com.afterroot.tmdbapi2.api.AuthApi
import com.afterroot.tmdbapi2.model.RequestBodyToken
import com.afterroot.tmdbapi2.model.ResponseRequestToken
import okhttp3.HttpUrl

class AuthRepository(val authApi: AuthApi) {

    /**
     * This function generates a new request token that you can ask a user to approve.
     * This is the first step in getting permission from a user to read and write data
     * on their behalf. You can read more about this system by clicking link in see also
     * @see <a href="https://developers.themoviedb.org/4/auth/user-authorization-1">User Authorization</a>
     * @param requestBodyToken Optional body to post with request containing redirect parameter
     * @see <a href="https://developers.themoviedb.org/4/auth/create-request-token">API Reference</a>
     */
    suspend fun createRequestToken(requestBodyToken: RequestBodyToken = RequestBodyToken()): ResponseRequestToken {
        return authApi.createRequestToken(requestBodyToken)
    }

    companion object {
        /**
         * In order for a user to approve [ResponseRequestToken], direct user to TMDB Website
         */
        fun getAuthVerifyUrl(token: ResponseRequestToken) = HttpUrl.Builder().scheme("https").host("www.themoviedb.org")
            .addPathSegment("auth").addPathSegment("access")
            .addQueryParameter("request_token", token.requestToken).build().toString()
    }
}
