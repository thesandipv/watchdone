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
package com.afterroot.tmdbapi

import com.afterroot.tmdbapi.model.core.ResponseStatus
import com.afterroot.tmdbapi.model.core.ResponseStatusException
import com.afterroot.tmdbapi.tools.ApiUrl
import com.afterroot.tmdbapi.tools.MovieDbException
import com.afterroot.tmdbapi.tools.RequestMethod
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException

abstract class AbstractTmdbApi internal constructor(protected val tmdbApi: TmdbApi) {
    fun <T> mapJsonResult(apiUrl: ApiUrl?, someClass: Class<T>?): T {
        return mapJsonResult(apiUrl, someClass, null)
    }

    fun <T> mapJsonResult(apiUrl: ApiUrl?, someClass: Class<T>?, jsonBody: String?): T {
        return mapJsonResult(apiUrl, someClass, jsonBody, RequestMethod.GET)
    }

    @Suppress("UNUSED_PARAMETER")
    fun <T> mapJsonResult(apiUrl: ApiUrl?, someClass: Class<T>?, jsonBody: String?, requestMethod: String): T {
        val webpage = tmdbApi.requestWebPage(apiUrl!!, requestMethod)

        return try {
            // check if was error responseStatus
            var responseStatus =
                jsonMapper.readValue(webpage, ResponseStatus::class.java)
            // work around the problem that there's no status code for suspected spam names yet
            val suspectedSpam = "Unable to create list because: Description is suspected to be spam."
            if (webpage!!.contains(suspectedSpam)) {
                responseStatus = ResponseStatus(-100, suspectedSpam)
            }

            // if null, the json response was not a error responseStatus code, and but something else
            val statusCode = responseStatus.statusCode
            if (statusCode != null && !SUCCESS_STATUS_CODES.contains(statusCode)) {
                throw ResponseStatusException(responseStatus)
            }
            jsonMapper.readValue(webpage, someClass)
        } catch (ex: IOException) {
            throw MovieDbException("mapping failed:\n$webpage")
        }
    }

    companion object {
        const val PARAM_YEAR = "year"
        const val PARAM_PAGE = "page"
        const val PARAM_LANGUAGE = "language"
        const val PARAM_ID = "id"
        const val PARAM_ADULT = "include_adult"
        const val PARAM_API_KEY = "api_key"

        @JvmField
        val jsonMapper = ObjectMapper()

        // see https://www.themoviedb.org/documentation/api/status-codes
        private val SUCCESS_STATUS_CODES: Collection<Int> = listOf(
            1, // Success
            12, // The item/record was updated successfully.
            13 // The item/record was updated successfully.
        )
    }
}
