package com.afterroot.tmdbapi.model.people

import com.fasterxml.jackson.annotation.JsonProperty

class PersonCast : Person() {
    @JsonProperty("character")
    var character: String? = null

    @JsonProperty("order")
    var order = 0
}