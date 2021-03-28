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
package com.afterroot.tmdbapi.tools

import com.afterroot.tmdbapi.AbstractTmdbApi
import org.apache.commons.lang3.StringUtils
import java.net.URL
import java.net.URLEncoder
import java.util.ArrayList
import java.util.HashMap

/**
 * Tmdb Api URL Builder
 *
 * @author Holger Brandl
 */
class ApiUrl(vararg urlElements: Any?) {
    private val baseUrl: String
    private val params: MutableMap<String, String> =
        HashMap()

    fun buildUrl(): URL {
        val urlBuilder = StringBuilder(baseUrl)
        return try {
            if (params.isNotEmpty()) {
                val keys: List<String> = ArrayList(params.keys)
                for (i in keys.indices) {
                    urlBuilder.append(if (i == 0) "?" else "&")
                    val paramName = keys[i]
                    urlBuilder.append(paramName).append("=")
                    urlBuilder.append(URLEncoder.encode(params[paramName], "UTF-8"))
                }
            }
            URL(urlBuilder.toString())
        } catch (e: Throwable) {
            throw RuntimeException(e)
        }
    }

    fun addParam(name: String, value: Any) {
        addParam(name, value.toString())
    }

    fun addParam(name: String, value: String) {
        if (params.containsKey(name)) {
            throw RuntimeException("paramater '$name' already defined")
        }
        val mName: String = StringUtils.trimToEmpty(name)
        if (name.isEmpty()) {
            throw RuntimeException("parameter name can not be empty")
        }
        val mValue: String = StringUtils.trimToEmpty(value)
        if (value.isEmpty()) {
            throw RuntimeException("value of parameter '$name' can not be empty")
        }
        params[mName] = mValue
    }

    /**
     * Add argument
     *
     * @param key
     * @param value
     */
    fun addParam(key: String, value: Int) {
        addParam(key, value.toString())
    }

    /**
     * Add argument
     *
     * @param key
     * @param value
     */
    fun addParam(key: String, value: Boolean) {
        addParam(key, java.lang.Boolean.toString(value))
    }

    /**
     * Convenience wrapper around addArgument
     *
     * @param appendToResponse Comma separated, any movie method
     */
    fun appendToResponse(vararg appendToResponse: String?) {
        if (appendToResponse.isEmpty()) {
            return
        }
        addParam(APPEND_TO_RESPONSE, StringUtils.join(appendToResponse, ","))
    }

    fun addPage(page: Int?) {
        if (page != null && page > 0) {
            addParam(AbstractTmdbApi.PARAM_PAGE, page)
        }
    }

    fun addLanguage(language: String) {
        if (StringUtils.isNotBlank(language)) {
            addParam(AbstractTmdbApi.PARAM_LANGUAGE, language)
        }
    }

    companion object {
        /*
     * TmdbApi API Base URL
     */
        private const val TMDB_API_BASE = "https://api.themoviedb.org/3/"
        private const val APPEND_TO_RESPONSE = "append_to_response"
    }

    //
    //    public ApiUrl(String apiKey, Object... urlElements) {
    //        this(urlElements);
    //
    //        assert StringUtils.isNotBlank(apiKey);
    //        addParam(PARAM_API_KEY, apiKey);
    //
    //    }
    init {
        val baseUrlBuilder = StringBuilder(TMDB_API_BASE)
        for (i in urlElements.indices) {
            baseUrlBuilder.append(urlElements[i])
            if (i < urlElements.size - 1) {
                baseUrlBuilder.append("/")
            }
        }
        baseUrl = baseUrlBuilder.toString()
    }
}
