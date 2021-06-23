package com.afterroot.tmdbapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.afterroot.tmdbapi.model.core.AbstractJsonMapping;
import com.afterroot.tmdbapi.model.people.Person;
import com.afterroot.tmdbapi.model.tv.TvSeries;

import java.util.List;


public class FindResults extends AbstractJsonMapping {

    @JsonProperty("movie_results")
    private List<NetworkMovie> movieResults;

    @JsonProperty("person_results")
    private List<Person> personResults;

    @JsonProperty("tv_results")
    private List<TvSeries> tvResults;


    public List<NetworkMovie> getMovieResults() {
        return movieResults;
    }


    public List<Person> getPersonResults() {
        return personResults;
    }


    public List<TvSeries> getTvResults() {
        return tvResults;
    }


    public void setMovieResults( List<NetworkMovie> movieResults ) {
        this.movieResults = movieResults;
    }


    public void setPersonResults( List<Person> personResults ) {
        this.personResults = personResults;
    }


    public void setTvResults( List<TvSeries> tvResults ) {
        this.tvResults = tvResults;
    }
}
