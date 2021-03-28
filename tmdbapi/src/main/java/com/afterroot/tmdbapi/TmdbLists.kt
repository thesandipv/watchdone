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

import com.afterroot.tmdbapi.model.ListItemStatus
import com.afterroot.tmdbapi.model.MovieList
import com.afterroot.tmdbapi.model.MovieListCreationStatus
import com.afterroot.tmdbapi.model.core.ResponseStatus
import com.afterroot.tmdbapi.model.core.SessionToken
import com.afterroot.tmdbapi.tools.ApiUrl
import com.afterroot.tmdbapi.tools.RequestMethod
import org.apache.commons.lang3.StringUtils
import java.util.Collections
import java.util.HashMap

class TmdbLists(tmdbApi: TmdbApi?) : AbstractTmdbApi(tmdbApi!!) {
    /**
     * Get a list by its ID
     *
     * @param listId
     * @return The list and its items
     */
    fun getList(listId: String?): MovieList {
        val apiUrl = ApiUrl(TMDB_METHOD_LIST, listId)
        return mapJsonResult(apiUrl, MovieList::class.java)
    }

    /**
     * This method lets users create a new list. A valid session id is required.
     *
     * @return The list id
     */
    fun createList(sessionToken: SessionToken?, name: String?, description: String?): String {
        val apiUrl = ApiUrl(TMDB_METHOD_LIST)
        apiUrl.addParam(TmdbAccount.PARAM_SESSION, sessionToken!!)
        val body = HashMap<String, String>()
        body["name"] = StringUtils.trimToEmpty(name)
        body["description"] = StringUtils.trimToEmpty(description)
        val jsonBody = Utils.convertToJson(jsonMapper, body)
        return mapJsonResult(apiUrl, MovieListCreationStatus::class.java, jsonBody).listId
    }

    /**
     * Check to see if a movie ID is already added to a list.
     *
     * @return true if the movie is on the list
     */
    fun isMovieOnList(listId: String?, movieId: Int?): Boolean {
        val apiUrl = ApiUrl(TMDB_METHOD_LIST, listId, "item_status")
        apiUrl.addParam("movie_id", movieId!!)
        return mapJsonResult(apiUrl, ListItemStatus::class.java).isItemPresent
    }

    /**
     * This method lets users add new movies to a list that they created. A valid session id is required.
     *
     * @return true if the movie is on the list
     */
    fun addMovieToList(
        sessionToken: SessionToken,
        listId: String,
        movieId: Int
    ): ResponseStatus {
        return modifyMovieList(sessionToken, listId, movieId, "add_item")
    }

    /**
     * This method lets users remove movies from a list that they created. A valid session id is required.
     *
     * @return true if the movie is on the list
     */
    fun removeMovieFromList(
        sessionToken: SessionToken,
        listId: String,
        movieId: Int
    ): ResponseStatus {
        return modifyMovieList(sessionToken, listId, movieId, "remove_item")
    }

    private fun modifyMovieList(
        sessionToken: SessionToken,
        listId: String,
        movieId: Int,
        operation: String
    ): ResponseStatus {
        val apiUrl = ApiUrl(TMDB_METHOD_LIST, listId, operation)
        apiUrl.addParam(TmdbAccount.PARAM_SESSION, sessionToken)
        val jsonBody = Utils.convertToJson(
            jsonMapper,
            Collections.singletonMap("media_id", movieId.toString() + "")
        )
        return mapJsonResult(
            apiUrl,
            ResponseStatus::class.java,
            jsonBody
        )
    }

    /**
     * This method lets users delete a list that they created. A valid session id is required.
     */
    fun deleteMovieList(
        sessionToken: SessionToken?,
        listId: String?
    ): ResponseStatus {
        val apiUrl = ApiUrl(TMDB_METHOD_LIST, listId)
        apiUrl.addParam(TmdbAccount.PARAM_SESSION, sessionToken!!)
        return mapJsonResult(
            apiUrl,
            ResponseStatus::class.java,
            null,
            RequestMethod.DELETE
        )
    }

    companion object {
        const val TMDB_METHOD_LIST = "list"
    }
}
