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
