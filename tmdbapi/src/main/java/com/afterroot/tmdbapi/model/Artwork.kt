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

import com.afterroot.tmdbapi.model.core.AbstractJsonMapping
import com.fasterxml.jackson.annotation.JsonProperty

data class Artwork(
    @JsonProperty("iso_639_1")
    var language: String? = null,
    @JsonProperty("file_path")
    var filePath: String? = null,
    @JsonProperty("aspect_ratio")
    var aspectRatio: Float = 0f,
    @JsonProperty("height")
    var height: Int = 0,
    @JsonProperty("width")
    var width: Int = 0,
    @JsonProperty("vote_average")
    var voteAverage: Float = 0f,
    @JsonProperty("vote_count")
    var voteCount: Int = 0,
    @JsonProperty("flag")
    var flag: String? = null
) : AbstractJsonMapping() {
    var artworkType = ArtworkType.POSTER
}
