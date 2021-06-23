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
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import org.apache.commons.lang3.StringUtils

@JsonRootName("collection")
data class Collection(
    @JsonProperty("title")
    private var title: String? = null,
    @JsonProperty("name")
    private var name: String? = null,
    @JsonProperty("poster_path")
    var posterPath: String? = null,
    @JsonProperty("backdrop_path")
    var backdropPath: String? = null,
    @JsonProperty("release_date")
    var releaseDate: String? = null
) : IdElement() {

    fun getTitle(): String? {
        return if (StringUtils.isBlank(title)) {
            name
        } else title
    }

    fun getName(): String? {
        return if (StringUtils.isBlank(name)) {
            title
        } else name
    }
}
