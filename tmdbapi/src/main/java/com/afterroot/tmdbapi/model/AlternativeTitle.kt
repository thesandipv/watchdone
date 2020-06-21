package com.afterroot.tmdbapi.model

import com.afterroot.tmdbapi.model.core.AbstractJsonMapping
import com.fasterxml.jackson.annotation.JsonProperty

data class AlternativeTitle(
    @JsonProperty("iso_3166_1")
    var country: String? = null,
    @JsonProperty("title")
    var title: String? = null
) : AbstractJsonMapping()