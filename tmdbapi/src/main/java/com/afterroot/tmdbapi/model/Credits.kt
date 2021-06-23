/*
 * Copyright (C) 2020-2021 Sandip Vaghela
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.afterroot.tmdbapi.model

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
                crew?.let { addAll(it) }
                cast?.let { addAll(it) }
                guestStars?.let { addAll(it) }
            }
            return involved
        }
}
