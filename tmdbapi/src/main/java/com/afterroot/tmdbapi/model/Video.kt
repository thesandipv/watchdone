package com.afterroot.tmdbapi.model

import com.afterroot.tmdbapi.model.core.IdElement
import com.afterroot.tmdbapi.model.core.NamedStringIdElement
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName

@JsonRootName("video")
data class Video(
        @JsonProperty("site")
        var site: String? = null,
        @JsonProperty("key")
        var key: String? = null,
        @JsonProperty("size")
        var size: Int? = null,
        @JsonProperty("type")
        var type: String? = null
) : NamedStringIdElement() {
    inner class Results : IdElement() {
        @JsonProperty("results")
        val videos: List<Video>? = null
    }
}