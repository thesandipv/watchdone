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
    JsonSubTypes.Type(value = MovieDbOld::class, name = "movie"),
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