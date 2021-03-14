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

package com.afterroot.tmdbapi2.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.afterroot.tmdbapi.model.core.AbstractJsonMapping
import com.fasterxml.jackson.annotation.JsonProperty

data class Genres(
    @JsonProperty("genres")
    val genres: List<Genre> = listOf()
) : AbstractJsonMapping()

@Entity(tableName = Genre.TABLE_NAME)
data class Genre(
    @JsonProperty("id")
    @PrimaryKey
    val id: Int = 0,
    @JsonProperty("name")
    val name: String = ""
) : AbstractJsonMapping() {
    companion object {
        const val TABLE_NAME = "genres"
    }
}