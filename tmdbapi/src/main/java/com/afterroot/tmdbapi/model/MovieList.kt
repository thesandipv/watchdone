package com.afterroot.tmdbapi.model

import com.afterroot.tmdbapi.model.core.NamedStringIdElement
import com.fasterxml.jackson.annotation.JsonProperty

class MovieList : NamedStringIdElement() {
    @JsonProperty("created_by")
    var createdBy: String? = null

    @JsonProperty("description")
    var description: String? = null

    @JsonProperty("favorite_count")
    var favoriteCount: Int = 0

    @JsonProperty("item_count")
    var itemCount: Int = 0

    @JsonProperty("poster_path")
    var posterPath: String? = null

    @JsonProperty("list_type")
    var listType: String? = null

    //used for /list
    @JsonProperty("items")
    var items: List<MovieDb>? = null
}