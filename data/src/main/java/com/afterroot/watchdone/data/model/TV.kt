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
package com.afterroot.watchdone.data.model

import com.afterroot.tmdbapi.model.ContentRating
import com.afterroot.tmdbapi.model.Credits
import com.afterroot.tmdbapi.model.ExternalIds
import com.afterroot.tmdbapi.model.MovieImages
import com.afterroot.tmdbapi.model.Multi
import com.afterroot.tmdbapi.model.Video
import com.afterroot.tmdbapi.model.core.ResultsPage
import com.afterroot.tmdbapi.model.keywords.Keyword
import com.afterroot.tmdbapi.model.people.Person
import com.afterroot.tmdbapi.model.tv.Network
import com.afterroot.tmdbapi.model.tv.TvSeason
import com.afterroot.tmdbapi.model.tv.TvSeries
import com.afterroot.tmdbapi2.model.Genre

data class TV(
    // Info
    val id: Int = 0,
    val name: String? = null,
    var createdBy: List<Person>? = null,
    var episodeRuntime: List<Int>? = null,
    var firstAirDate: String? = null,
    var lastAirDate: String? = null,
    var genres: List<Genre>? = null,
    var homepage: String? = null,
    var originalName: String? = null,
    var originCountry: List<String>? = null,
    var networks: List<Network>? = null,
    var overview: String? = null,
    var popularity: Float = 0f,
    var backdropPath: String? = null,
    var posterPath: String? = null,
    var numberOfEpisodes: Int = 0,
    var numberOfSeasons: Int = 0,
    var seasons: List<TvSeason>? = null,
    var recommendations: ResultsPage<TvSeries>? = null,
    var userRating: Float = 0f,
    val voteAverage: Double? = null,
    var voteCount: Int = 0,
    var status: String? = null,
    private var contentRatings: List<ContentRating>? = null,
    // Appendable responses
    val credits: Credits? = null,
    val externalIds: ExternalIds? = null,
    val images: MovieImages? = null,
    val videos: List<Video>? = null,
    val keywords: List<Keyword>? = null,
    // Additional Data
    var isWatched: Boolean = false
) : Multi {

    override val mediaType: Multi.MediaType
        get() = Multi.MediaType.TV_SERIES

    // Just for Firestore
    var releaseDate = firstAirDate
}
