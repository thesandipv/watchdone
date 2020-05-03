package com.afterroot.tmdbapi.model.core;

import com.afterroot.tmdbapi.model.keywords.Keyword;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class TvKeywords extends AbstractJsonMapping {


    @JsonProperty("results")
    private List<Keyword> results = new ArrayList<>();


    public List<Keyword> getKeywords() {
        return results;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.results = keywords;
    }
}
