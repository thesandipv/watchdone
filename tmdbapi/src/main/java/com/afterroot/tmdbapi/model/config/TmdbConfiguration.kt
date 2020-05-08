package com.afterroot.tmdbapi.model.config

import com.afterroot.tmdbapi.model.core.AbstractJsonMapping
import com.fasterxml.jackson.annotation.JsonProperty

data class TmdbConfiguration(
    @JsonProperty("base_url") var baseUrl: String? = null,
    @JsonProperty("secure_base_url") var secureBaseUrl: String? = null,
    @JsonProperty("poster_sizes") var posterSizes: List<String>? = null,
    @JsonProperty("backdrop_sizes") var backdropSizes: List<String>? = null,
    @JsonProperty("profile_sizes") var profileSizes: List<String>? = null,
    @JsonProperty("logo_sizes") var logoSizes: List<String>? = null
) : AbstractJsonMapping() {

    /**
     * Copy the data from the passed object to this one
     *
     * @param config
     */
    fun clone(config: TmdbConfiguration) {
        backdropSizes = config.backdropSizes
        baseUrl = config.baseUrl
        posterSizes = config.posterSizes
        profileSizes = config.profileSizes
        logoSizes = config.logoSizes
    }

    /**
     * Check that the poster size is valid
     *
     * @param posterSize
     */
    fun isValidPosterSize(posterSize: String): Boolean = if (posterSize.isBlank() || posterSizes!!.isEmpty()) {
        false
    } else posterSizes!!.contains(posterSize)

    /**
     * Check that the backdrop size is valid
     *
     * @param backdropSize
     */
    fun isValidBackdropSize(backdropSize: String): Boolean = if (backdropSize.isBlank() || backdropSizes!!.isEmpty()) {
        false
    } else backdropSizes!!.contains(backdropSize)

    /**
     * Check that the profile size is valid
     *
     * @param profileSize
     */
    fun isValidProfileSize(profileSize: String): Boolean = if (profileSize.isBlank() || profileSizes!!.isEmpty()) {
        false
    } else profileSizes!!.contains(profileSize)

    /**
     * Check that the logo size is valid
     *
     * @param logoSize
     */
    fun isValidLogoSize(logoSize: String): Boolean = if (logoSize.isBlank() || logoSizes!!.isEmpty()) {
        false
    } else logoSizes!!.contains(logoSize)

    /**
     * Check to see if the size is valid for any of the images types
     *
     * @param sizeToCheck
     */
    fun isValidSize(sizeToCheck: String): Boolean = isValidPosterSize(sizeToCheck)
            || isValidBackdropSize(sizeToCheck)
            || isValidProfileSize(sizeToCheck)
            || isValidLogoSize(sizeToCheck)
}