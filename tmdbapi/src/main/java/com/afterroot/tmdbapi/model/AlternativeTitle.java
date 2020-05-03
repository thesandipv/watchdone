package com.afterroot.tmdbapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.afterroot.tmdbapi.model.core.AbstractJsonMapping;


public class AlternativeTitle extends AbstractJsonMapping {

    @JsonProperty("iso_3166_1")
    private String country;
    @JsonProperty("title")
    private String title;


    public String getCountry() {
        return country;
    }


    public String getTitle() {
        return title;
    }


    public void setCountry(String country) {
        this.country = country;
    }


    public void setTitle(String title) {
        this.title = title;
    }
}
