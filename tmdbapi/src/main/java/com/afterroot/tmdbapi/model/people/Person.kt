package com.afterroot.tmdbapi.model.people

import com.afterroot.tmdbapi.model.core.NamedIdElement
import com.fasterxml.jackson.annotation.JsonProperty

open class Person : NamedIdElement() {
    @JsonProperty("cast_id")
    var castId = 0

    @JsonProperty("credit_id")
    var creditId: String? = null

    @JsonProperty("profile_path")
    open var profilePath: String? = ""

    @JsonProperty("adult")
    var isAdult = false

    @JsonProperty("gender")
    var gender = false

    @JsonProperty("known_for_department")
    var knownForDepartment: String? = null

    @JsonProperty("original_name")
    var originalName: String? = null

    @JsonProperty("popularity")
    var popularity = 0.0f
}