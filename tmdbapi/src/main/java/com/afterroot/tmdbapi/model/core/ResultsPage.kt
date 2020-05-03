package com.afterroot.tmdbapi.model.core

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.afterroot.tmdbapi.Types

open class ResultsPage<T> : AbstractJsonMapping(), Iterable<T> {
    @JsonProperty("results")
    lateinit var results: MutableList<T>

    @JsonProperty("page")
    var page = 0

    @JsonProperty("total_pages")
    var totalPages = 0

    @JsonProperty("total_results")
    var totalResults = 0

    @JsonIgnore
    override fun iterator(): MutableIterator<T> {
        return results.iterator()
    }

    @JsonIgnore
    fun type(): Int = Types.RESULT_PAGE
}