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

import com.afterroot.tmdbapi.model.MovieDb
import com.afterroot.tmdbapi.tools.MovieDbException
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.lang3.StringUtils
import java.net.MalformedURLException
import java.net.URL
import java.util.ArrayList

object Utils {
    /**
     * Compare the MovieDB object with a title & year
     *
     * @param moviedb The moviedb object to compare too
     * @param title   The title of the movie to compare
     * @param year    The year of the movie to compare exact match
     * @return True if there is a match, False otherwise.
     */
    @JvmOverloads
    fun compareMovies(moviedb: MovieDb?, title: String, year: String, maxDistance: Int = 0): Boolean {
        if (moviedb == null || StringUtils.isBlank(title)) {
            return java.lang.Boolean.FALSE
        }
        if (isValidYear(year) && isValidYear(moviedb.releaseDate)) {
            // Compare with year
            val movieYear = moviedb.releaseDate!!.substring(0, 4)
            if (movieYear == year) {
                if (compareDistance(moviedb.originalTitle, title, maxDistance)) {
                    return java.lang.Boolean.TRUE
                }
                if (compareDistance(moviedb.title, title, maxDistance)) {
                    return java.lang.Boolean.TRUE
                }
            }
        }

        // Compare without year
        if (compareDistance(moviedb.originalTitle, title, maxDistance)) {
            return java.lang.Boolean.TRUE
        }
        return if (compareDistance(moviedb.title, title, maxDistance)) {
            java.lang.Boolean.TRUE
        } else java.lang.Boolean.FALSE
    }

    /**
     * Compare the Levenshtein Distance between the two strings
     *
     * @param title1
     * @param title2
     * @param distance
     */
    private fun compareDistance(title1: String?, title2: String, distance: Int): Boolean {
        return StringUtils.getLevenshteinDistance(title1, title2) <= distance
    }

    /**
     * Check the year is not blank or UNKNOWN
     *
     * @param year
     */
    private fun isValidYear(year: String?): Boolean {
        return StringUtils.isNotBlank(year) && year != "UNKNOWN"
    }

    @JvmStatic
    fun sleep(timeMs: Int) {
        try {
            Thread.sleep(timeMs.toLong())
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Generate the full image URL from the size and image path
     *
     * @param tmdb
     * @param imagePath
     * @param requiredSize
     */
    @JvmStatic
    fun createImageUrl(tmdb: TmdbApi, imagePath: String?, requiredSize: String): URL? {
        if (StringUtils.isBlank(imagePath)) {
            return null
        }
        val configuration = tmdb.configuration
        if (!configuration.isValidSize(requiredSize)) {
            throw MovieDbException("Invalid size: $requiredSize")
        }
        val sb = StringBuilder(configuration.baseUrl)
        sb.append(requiredSize)
        sb.append(imagePath)
        return try {
            URL(sb.toString())
        } catch (ex: MalformedURLException) {
            throw MovieDbException(sb.toString(), ex)
        }
    }

    @JvmStatic
    fun <T> nullAsEmpty(items: List<T>?): List<T> {
        return items ?: ArrayList()
    }

    /**
     * Use Jackson to convert Map to json string.
     */
    @JvmStatic
    fun convertToJson(jsonMapper: ObjectMapper, map: Map<String, Any>?): String {
        return try {
            jsonMapper.writeValueAsString(map)
        } catch (jpe: JsonProcessingException) {
            throw RuntimeException("json conversion failed", jpe)
        }
    }

    fun <T> copyIterator(iter: Iterator<T>): List<T> {
        val copy: MutableList<T> = ArrayList()
        while (iter.hasNext()) copy.add(iter.next())
        return copy
    }

    @JvmStatic
    fun asStringArray(appendToResponse: Array<Any>?): Array<String?>? {
        if (appendToResponse == null || appendToResponse.size == 0) return null

        // guava would be more convenient here (just use transformer)
        val asArray = arrayOfNulls<String>(appendToResponse.size)
        for (i in appendToResponse.indices) {
            asArray[i] = appendToResponse[i].toString()
        }
        return asArray
    }

    fun parseInteger(valueOrNull: String?): Int? {
        return valueOrNull?.toInt()
    }
}
