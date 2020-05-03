package com.afterroot.tmdbapi.model.core;

import com.afterroot.tmdbapi.tools.MovieDbException;

public class ResponseStatusException extends MovieDbException {

    private final ResponseStatus responseStatus;


    public ResponseStatusException(ResponseStatus responseStatus) {
        super(responseStatus.getStatusCode() + " :: " + responseStatus.getStatusMessage());

        this.responseStatus = responseStatus;
    }


    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }


    @Override
    public String toString() {
        return responseStatus.toString();
    }
}
