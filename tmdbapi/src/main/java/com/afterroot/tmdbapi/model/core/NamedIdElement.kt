package com.afterroot.tmdbapi.model.core

import com.fasterxml.jackson.annotation.JsonProperty

open class NamedIdElement : IdElement() {
    @JsonProperty("name")
    var name: String? = null

    override fun toString(): String {
        return "$name [$id]"
    }
}