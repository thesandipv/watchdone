package com.afterroot.tmdbapi.model

import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle

data class Data(
    var title: String? = null,
    var overview: String? = null,
    var homepage: String? = null
) {
    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE)
    }
}