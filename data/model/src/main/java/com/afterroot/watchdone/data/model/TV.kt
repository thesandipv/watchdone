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

import com.afterroot.tmdbapi.model.Genre
import info.movito.themoviedbapi.model.ContentRating
import info.movito.themoviedbapi.model.Credits
import info.movito.themoviedbapi.model.ExternalIds
import info.movito.themoviedbapi.model.MovieImages
import info.movito.themoviedbapi.model.core.ResultsPage
import info.movito.themoviedbapi.model.people.Person
import info.movito.themoviedbapi.model.tv.Network
import info.movito.themoviedbapi.model.tv.TvSeries

data class TV(
    // Info
    override val id: Long = 0,
    override val tmdbId: Int? = null,
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
    var seasons: Seasons = null,
    var recommendations: ResultsPage<TvSeries>? = null,
    var userRating: Float = 0f,
    val voteAverage: Float? = null,
    var voteCount: Int = 0,
    var status: String? = null,
    private var contentRatings: List<ContentRating>? = null,
    // Appendable responses
    val credits: Credits? = null,
    val externalIds: ExternalIds? = null,
    val images: MovieImages? = null,
    val videos: Videos = null,
    val keywords: Keywords = null,
    // Additional Data
    override val isWatched: Boolean = false,
) : WDEntity, TmdbIdEntity, Watchable {

    val mediaType: MediaType
        get() = MediaType.SHOW

    // Just for Firestore
    var releaseDate = firstAirDate

    fun rating(): String = String.format("%.1f", voteAverage)

    companion object {
        val Empty = TV()
    }
}
