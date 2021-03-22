package com.afterroot.tmdbapi.model.tv

import com.afterroot.tmdbapi.model.ContentRating
import com.afterroot.tmdbapi.model.Multi
import com.afterroot.tmdbapi.model.core.ResultsPage
import com.afterroot.tmdbapi.model.people.Person
import com.afterroot.tmdbapi2.model.Genre
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo

/**
 * @author Sandip Vaghela
 * @author Holger Brandl
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
data class TvSeries(
    @JsonProperty("created_by")
    var createdBy: List<Person>? = null,

    @JsonProperty("episode_run_time")
    var episodeRuntime: List<Int>? = null,

    @JsonProperty("first_air_date")
    var firstAirDate: String? = null,

    @JsonProperty("last_air_date")
    var lastAirDate: String? = null,

    @JsonProperty("genres")
    var genres: List<Genre>? = null,

    @JsonProperty("homepage")
    var homepage: String? = null,

    @JsonProperty("original_name")
    var originalName: String? = null,

    @JsonProperty("origin_country")
    var originCountry: List<String>? = null,

    @JsonProperty("networks")
    var networks: List<Network>? = null,

    @JsonProperty("overview")
    var overview: String? = null,

    @JsonProperty("popularity")
    var popularity: Float = 0f,

    @JsonProperty("backdrop_path")
    var backdropPath: String? = null,

    @JsonProperty("poster_path")
    var posterPath: String? = null,

    @JsonProperty("number_of_episodes")
    var numberOfEpisodes: Int = 0,

    @JsonProperty("number_of_seasons")
    var numberOfSeasons: Int = 0,

    @JsonProperty("seasons")
    var seasons: List<TvSeason>? = null,

    @JsonProperty("recommendations")
    var recommendations: ResultsPage<TvSeries>? = null,

    @JsonProperty("rating")
    var userRating: Float = 0f,

    @JsonProperty("vote_average")
    val voteAverage: Double? = null,

    @JsonProperty("vote_count")
    var voteCount: Int = 0,

    @JsonProperty("status")
    var status: String? = null,

    @JsonProperty("content_ratings")
    private var contentRatings: ContentRating.Results? = null
) : AbstractTvElement(), Multi {

    override val mediaType: Multi.MediaType
        get() = Multi.MediaType.TV_SERIES

    fun getContentRatings(): List<ContentRating>? {
        return contentRatings?.contentRatings
    }

    fun setContentRatings(contentRatings: ContentRating.Results?) {
        this.contentRatings = contentRatings
    }
}