package com.afterroot.tmdbapi.model.people

import com.fasterxml.jackson.annotation.JsonProperty

class PersonCrew : Person() {
    @JsonProperty("department")
    var department: String? = null

    @JsonProperty("job")
    var job: String? = null
}