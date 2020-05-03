package com.afterroot.tmdbapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.afterroot.tmdbapi.model.core.ResponseStatus;


public class MovieListCreationStatus extends ResponseStatus {

    @JsonProperty("list_id")
    private String listId;


    public String getListId() {
        return listId;
    }


    public void setListId( String listId ) {
        this.listId = listId;
    }
}
