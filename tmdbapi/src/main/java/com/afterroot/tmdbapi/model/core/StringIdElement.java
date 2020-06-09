package com.afterroot.tmdbapi.model.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;


/**
 * Base class for json wrappers with id element
 *
 * @author Holger Brandl
 */
public class StringIdElement extends AbstractJsonMapping implements Serializable {

    @JsonProperty("id")
    private String id;


    public String getId() {
        return id;
    }


    public void setId( String id ) {
        this.id = id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringIdElement that = (StringIdElement) o;

        return id.equals(that.id);
    }


    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
