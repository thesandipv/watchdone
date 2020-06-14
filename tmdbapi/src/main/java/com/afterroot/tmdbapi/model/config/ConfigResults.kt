package com.afterroot.tmdbapi.model.config

import com.afterroot.tmdbapi.model.core.AbstractJsonMapping
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class ConfigResults : AbstractJsonMapping(), Serializable {
    @JsonProperty("images")
    var imagesConfig: ImagesConfig? = null

    @JsonProperty("change_keys")
    var changeKeys: List<String>? = null
}