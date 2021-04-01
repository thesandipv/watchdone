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

import com.afterroot.tmdbapi.TmdbMovies
import com.afterroot.tmdbapi.model.AlternativeTitle
import com.afterroot.tmdbapi.model.Collection
import com.afterroot.tmdbapi.model.Credits
import com.afterroot.tmdbapi.model.Language
import com.afterroot.tmdbapi.model.MovieImages
import com.afterroot.tmdbapi.model.MovieList
import com.afterroot.tmdbapi.model.MovieTranslations
import com.afterroot.tmdbapi.model.Multi
import com.afterroot.tmdbapi.model.NetworkMovie
import com.afterroot.tmdbapi.model.ProductionCompany
import com.afterroot.tmdbapi.model.ProductionCountry
import com.afterroot.tmdbapi.model.Reviews
import com.afterroot.tmdbapi.model.Video
import com.afterroot.tmdbapi.model.core.MovieKeywords
import com.afterroot.tmdbapi.model.core.ResultsPage
import com.afterroot.tmdbapi2.model.Genre

data class Movie(
    // Movie Info
    val id: Int = 0,
    val adult: Boolean? = null,
    val backdropPath: String? = null,
    val belongsToCollection: Collection? = null,
    val budget: Int? = null,
    val genreIds: List<Int>? = null,
    val genres: List<Genre>? = null,
    val homepage: String? = null,
    val imdbId: String? = null,
    val originalLanguage: String? = null,
    val originalTitle: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    val posterPath: String? = null,
    val productionCompanies: List<ProductionCompany>? = null,
    val productionCountries: List<ProductionCountry>? = null,
    val releaseDate: String? = null,
    val revenue: Long? = null,
    val runtime: Int? = null,
    val spokenLanguages: List<Language>? = null,
    val status: String? = null,
    val tagline: String? = null,
    val title: String? = null,
    val video: Boolean? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
    var userRating: Float = 0f,
    // Appendable responses
    private val recommendedMovies: ResultsPage<NetworkMovie>? = null,
    private var alternativeTitles: List<AlternativeTitle>? = null,
    private var images: MovieImages? = null,
    private var keywords: MovieKeywords? = null,
    private var lists: ResultsPage<MovieList>? = null,
    private var releases: TmdbMovies.ReleaseInfoResults? = null,
    private var reviews: ResultsPage<Reviews>? = null,
    private var similarMovies: ResultsPage<NetworkMovie>? = null,
    private var translations: MovieTranslations? = null,
    private var videos: Video.Results? = null,
    var credits: Credits? = null,
    // Additional Data
    var isWatched: Boolean = false
) : Multi {
    override val mediaType: Multi.MediaType
        get() = Multi.MediaType.MOVIE
}
