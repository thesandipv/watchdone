package com.afterroot.tmdbapi.model.core

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * Base class for json wrappers with id element
 *
 * @author Holger Brandl
 */
open class IdElement : AbstractJsonMapping(), Serializable {
    @JsonProperty("id")
    var id = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val idElement = other as IdElement
        return id == idElement.id
    }

    override fun hashCode(): Int {
        return id
    }
}