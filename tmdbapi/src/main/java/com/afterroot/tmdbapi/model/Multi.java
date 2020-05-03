package com.afterroot.tmdbapi.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.afterroot.tmdbapi.model.people.PersonPeople;
import com.afterroot.tmdbapi.model.tv.TvSeries;


/**
 * Interface that is needed for /search/multi request
 * <p>
 * {@link com.afterroot.tmdbapi.model.MovieDb}, {@link com.afterroot.tmdbapi.model.people.Person} and
 * {@link com.afterroot.tmdbapi.model.tv.TvSeries} implement this interface.</p>
 * <p>Each of them returns corresponding {@link MediaType}</p>
 *
 * @see com.afterroot.tmdbapi.TmdbSearch#searchMulti(String, String, Integer)
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "media_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MovieDb.class, name = "movie"),
        @JsonSubTypes.Type(value = PersonPeople.class, name = "person"),
        @JsonSubTypes.Type(value = TvSeries.class, name = "tv")})
public interface Multi {

    public enum MediaType {
        MOVIE,
        PERSON,
        TV_SERIES;
    }

    /**
     * Used to determine type Multi object without {@code instanceof()} or {@code getClass}
     */
    public MediaType getMediaType();
}

