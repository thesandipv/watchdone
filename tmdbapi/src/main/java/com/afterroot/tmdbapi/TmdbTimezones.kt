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

import com.afterroot.tmdbapi.model.core.AbstractJsonMapping
import com.afterroot.tmdbapi.tools.ApiUrl
import com.fasterxml.jackson.annotation.JsonProperty

class TmdbTimezones internal constructor(tmdbApi: TmdbApi?) : AbstractTmdbApi(tmdbApi!!) {
    val timezones: TimeZones
        get() {
            val apiUrl = ApiUrl(TMDB_METHOD_TIMEZONESLIST)
            val webpage = tmdbApi.requestWebPage(apiUrl)
            return jsonMapper.readValue(webpage, TimeZones::class.java)
        }

    companion object {
        const val TMDB_METHOD_TIMEZONESLIST = "configuration/timezones"
    }

    data class TimeZone(@JsonProperty("iso_3166_1") val country: String, @JsonProperty("zones") val zones: Array<String>) :
        AbstractJsonMapping() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TimeZone

            if (country != other.country) return false
            if (!zones.contentEquals(other.zones)) return false

            return true
        }

        fun type(): Int = Types.TIMEZONE

        override fun hashCode(): Int {
            var result = country.hashCode()
            result = 31 * result + zones.contentHashCode()
            return result
        }
    }

    data class TimeZones(val list: Array<TimeZone>) : AbstractJsonMapping() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        fun type(): Int = Types.TIMEZONE_LIST

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
}
