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
package com.afterroot.tmdbapi2.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ResponseRequestToken(
    @JsonProperty("request_token")
    val requestToken: String = "",
    @JsonProperty("status_code")
    val statusCode: Int = 0,
    @JsonProperty("status_message")
    val statusMessage: String = "",
    @JsonProperty("success")
    val success: Boolean = false
)

data class RequestBodyToken(
    @JsonProperty("redirect_to")
    val redirect_to: String = ""
)

data class ResponseAccessToken(
    @JsonProperty("access_token")
    val accessToken: String = "",
    @JsonProperty("account_id")
    val accountId: String = "",
    @JsonProperty("status_code")
    val statusCode: Int = 0,
    @JsonProperty("status_message")
    val statusMessage: String = "",
    @JsonProperty("success")
    val success: Boolean = false
)

data class RequestDeleteAccessToken(
    @JsonProperty("access_token")
    val accessToken: String = ""
)

data class ResponseDeleteAccessToken(
    @JsonProperty("status_code")
    val statusCode: Int = 0,
    @JsonProperty("status_message")
    val statusMessage: String = "",
    @JsonProperty("success")
    val success: Boolean = false
)
