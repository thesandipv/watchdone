package com.afterroot.tmdbapi.model

import com.afterroot.tmdbapi.model.core.NamedIdElement
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle

@JsonRootName("production_company")
class ProductionCompany : NamedIdElement() {
    @JsonProperty("logo_path")
    var logoPath: String? = null

    @JsonProperty("origin_country")
    var originCountry: String? = null

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE)
    }
}