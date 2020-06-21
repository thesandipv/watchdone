package com.afterroot.tmdbapi.model

import com.afterroot.tmdbapi.Utils.nullAsEmpty
import com.afterroot.tmdbapi.model.core.IdElement
import com.afterroot.tmdbapi.model.people.Person
import com.afterroot.tmdbapi.model.people.PersonCast
import com.afterroot.tmdbapi.model.people.PersonCrew
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.ArrayList

data class Credits(
    @JsonProperty("crew")
    var crew: List<PersonCrew>? = null,
    @JsonProperty("cast")
    var cast: List<PersonCast>? = null,
    @JsonProperty("guest_stars")
    var guestStars: List<PersonCast>? = null
) : IdElement() {
    /**
     * Convenience wrapper to get all people involved in the movie>
     */
    val all: List<Person>
        get() {
            val involved: MutableList<Person> = ArrayList()
            involved.apply {
                addAll(nullAsEmpty(crew))
                addAll(nullAsEmpty(cast))
                addAll(nullAsEmpty(guestStars))
            }
            return involved
        }
}