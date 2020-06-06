package com.afterroot.tmdbapi.model

import com.afterroot.tmdbapi.model.core.AbstractJsonMapping
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName

@JsonRootName("production_country")
data class ProductionCountry(
    @JsonProperty("iso_3166_1")
    var isoCode: String? = null,
    @JsonProperty("name")
    var name: String? = null
) : AbstractJsonMapping()