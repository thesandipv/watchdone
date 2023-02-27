/*
 * Copyright (C) 2020-2023 Sandip Vaghela
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
package com.afterroot.watchdone.data.model

import info.movito.themoviedbapi.model.Credits
import info.movito.themoviedbapi.model.ExternalIds
import info.movito.themoviedbapi.model.MovieImages
import info.movito.themoviedbapi.model.Video
import info.movito.themoviedbapi.model.keywords.Keyword

data class Episode(
    val id: Int = 0,
    val name: String? = null,
    val airDate: String? = null,
    val episodeNumber: Int = 0,
    val overview: String? = null,
    val seasonNumber: Int? = null,
    val seriesId: Int = 0,
    val stillPath: String? = null,
    val userRating: Float = 0f,
    val voteAverage: Float = 0f,
    val voteCount: Int = 0,
    val credits: Credits? = null,
    val externalIds: ExternalIds? = null,
    val images: MovieImages? = null,
    val videos: List<Video>? = null,
    val keywords: List<Keyword>? = null,
    // Additional Data
    var isWatched: Boolean = false
)
