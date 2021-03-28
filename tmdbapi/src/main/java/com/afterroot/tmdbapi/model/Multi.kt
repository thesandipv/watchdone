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
package com.afterroot.tmdbapi.model

import com.afterroot.tmdbapi.model.Multi.MediaType
import com.afterroot.tmdbapi.model.people.PersonPeople
import com.afterroot.tmdbapi.model.tv.TvSeries
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

/**
 * Interface that is needed for /search/multi request
 *
 *
 * [com.afterroot.tmdbapi.model.MovieDb], [com.afterroot.tmdbapi.model.people.Person] and
 * [com.afterroot.tmdbapi.model.tv.TvSeries] implement this interface.
 *
 * Each of them returns corresponding [MediaType]
 *
 * @see com.afterroot.tmdbapi.TmdbSearch.searchMulti
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "media_type")
@JsonSubTypes(
    JsonSubTypes.Type(value = MovieDb::class, name = "movie"),
    JsonSubTypes.Type(value = MovieDb::class, name = "movie"),
    JsonSubTypes.Type(value = PersonPeople::class, name = "person"),
    JsonSubTypes.Type(value = TvSeries::class, name = "tv")
)
interface Multi {
    enum class MediaType {
        MOVIE, PERSON, TV_SERIES
    }

    /**
     * Used to determine type Multi object without `instanceof()` or `getClass`
     */
    val mediaType: MediaType?
}
