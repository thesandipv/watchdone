package com.afterroot.tmdbapi.model

import com.afterroot.tmdbapi.AbstractTmdbApi
import com.afterroot.tmdbapi.model.keywords.Keyword
import org.apache.commons.lang3.StringUtils
import java.util.HashMap

/**
 * Generate a discover object for use in the MovieDbApi
 *
 *
 * This allows you to just add the search components you are concerned with
 *
 * @author stuart.boston
 * @author Sandip Vaghela
 */
class Discover {
    /**
     * Get the parameters
     *
     * This will be used to construct the URL in the API
     *
     * @return [Map] of Parameters
     */
    val params: MutableMap<String, String> = HashMap()

    /**
     * Minimum value is 1 if included.
     *
     * @param page
     * @return
     */
    fun page(page: Int?): Discover {
        if (page != null && page > 0) {
            params[AbstractTmdbApi.PARAM_PAGE] = page.toString()
        }
        return this
    }

    /**
     * ISO 639-1 code
     *
     * @param language
     * @return
     */
    fun language(language: String): Discover {
        if (StringUtils.isNotBlank(language)) {
            params[AbstractTmdbApi.PARAM_LANGUAGE] = language
        }
        return this
    }

    /**
     * Available options are
     * [SORT_BY_ORIGINAL_TITLE_ASC],
     * [SORT_BY_ORIGINAL_TITLE_DSC],
     * [SORT_BY_POP_ASC],
     * [SORT_BY_POP_DES],
     * [SORT_BY_PRIMARY_RELEASE_DATE_ASC],
     * [SORT_BY_PRIMARY_RELEASE_DATE_DES],
     * [SORT_BY_REL_ASC],
     * [SORT_BY_REL_DES],
     * [SORT_BY_REV_ASC],
     * [SORT_BY_REV_DES],
     * [SORT_BY_VOTE_AVG_ASC],
     * [SORT_BY_VOTE_AVG_DES],
     * [SORT_BY_VOTE_COUNT_ASC],
     * [SORT_BY_VOTE_COUNT_DES]
     *
     * @param sortBy Default [SORT_BY_POP_DES]
     * @return [Discover] Object with this parameter
     */
    fun sortBy(sortBy: String = SORT_BY_POP_DES): Discover {
        params[PARAM_SORT_BY] = sortBy
        return this
    }

    /**
     * Toggle the inclusion of adult titles
     *
     * @param includeAdult
     * @return [Discover] Object with this parameter
     */
    fun includeAdult(includeAdult: Boolean): Discover {
        params[AbstractTmdbApi.PARAM_ADULT] = includeAdult.toString()
        return this
    }

    /**
     * Filter the results release dates to matches that include this value.
     *
     * @param year
     * @return
     */
    fun year(year: Int): Discover {
        if (checkYear(year)) {
            params[AbstractTmdbApi.PARAM_YEAR] = year.toString()
        }
        return this
    }

    /**
     * Filter the results so that only the primary release date year has this value
     *
     * @param primaryReleaseYear
     * @return
     */
    fun primaryReleaseYear(primaryReleaseYear: Int): Discover {
        if (checkYear(primaryReleaseYear)) {
            params[PARAM_PRIMARY_RELEASE_YEAR] = primaryReleaseYear.toString()
        }
        return this
    }

    /**
     * Only include movies that are equal to, or have a vote count higher than this value
     *
     * @param voteCountGte
     * @return
     */
    fun voteCountGte(voteCountGte: Int): Discover {
        if (voteCountGte > 0) {
            params[PARAM_VOTE_COUNT_GTE] = voteCountGte.toString()
        }
        return this
    }

    /**
     * Only include movies that are equal to, or have a higher average rating than this value
     *
     * @param voteAverageGte
     * @return
     */
    fun voteAverageGte(voteAverageGte: Float): Discover {
        if (voteAverageGte > 0) {
            params[PARAM_VOTE_AVERAGE_GTE] = voteAverageGte.toString()
        }
        return this
    }

    /**
     * Only include movies with the specified genres.
     *
     *
     * Expected value is an integer (the id of a genre).
     *
     *
     * Multiple values can be specified.
     *
     *
     * Comma separated indicates an 'AND' query, while a pipe (|) separated value indicates an 'OR'
     *
     * @param withGenres
     * @return
     */
    fun withGenres(withGenres: String): Discover {
        if (StringUtils.isNotBlank(withGenres)) {
            params[PARAM_WITH_GENRES] = withGenres
        }
        return this
    }

    /**
     * Only include movies with the specified keywords.
     *
     *
     * Expected value is an integer (the id of a keyword). Multiple values can be specified. Comma separated indicates an 'AND' query, while a pipe (|) separated value indicates an 'OR'.
     *
     *
     * Multiple values can be specified.
     *
     * @param keywords
     * @return
     */
    fun withKeywords(keywords: List<Keyword>?, orQuery: Boolean): Discover {
        params[PARAM_WITH_KEYWORDS] = keywords?.joinToString(if (orQuery) "|" else ",") {
            it.id.toString()
        }.toString()
        return this
    }

    /**
     * The minimum release to include.
     * Expected format is YYYY-MM-DD.
     *
     * @param releaseDateGte
     * @return
     */
    fun releaseDateGte(releaseDateGte: String): Discover {
        if (StringUtils.isNotBlank(releaseDateGte)) {
            params[PARAM_RELEASE_DATE_GTE] = releaseDateGte
        }
        return this
    }

    /**
     * The maximum release to include.
     *
     *
     * Expected format is YYYY-MM-DD.
     *
     * @param releaseDateLte
     * @return
     */
    fun releaseDateLte(releaseDateLte: String): Discover {
        if (StringUtils.isNotBlank(releaseDateLte)) {
            params[PARAM_RELEASE_DATE_LTE] = releaseDateLte
        }
        return this
    }

    /**
     * Only include movies with certifications for a specific country.
     *
     *
     * When this value is specified, 'certificationLte' is required.
     *
     *
     * A ISO 3166-1 is expected
     *
     * @param certificationCountry
     * @return
     */
    fun certificationCountry(certificationCountry: String): Discover {
        if (StringUtils.isNotBlank(certificationCountry)) {
            params[PARAM_CERTIFICATION_COUNTRY] = certificationCountry
        }
        return this
    }

    /**
     * Only include movies with this certification and lower.
     *
     *
     * Expected value is a valid certification for the specified 'certificationCountry'.
     *
     * @param certificationLte
     * @return
     */
    fun certificationLte(certificationLte: String): Discover {
        if (StringUtils.isNotBlank(certificationLte)) {
            params[PARAM_CERTIFICATION_LTE] = certificationLte
        }
        return this
    }

    /**
     * Filter movies to include a specific company.
     *
     *
     * Expected value is an integer (the id of a company).
     *
     *
     * They can be comma separated to indicate an 'AND' query
     *
     * @param withCompanies
     * @return
     */
    fun withCompanies(withCompanies: String): Discover {
        if (StringUtils.isNotBlank(withCompanies)) {
            params[PARAM_WITH_COMPANIES] = withCompanies
        }
        return this
    }

    /**
     * check the year is between the min and max
     *
     * @param year year to check
     * @return 'true' if [year] in range otherwise 'false'
     */
    private fun checkYear(year: Int): Boolean {
        return year in YEAR_MIN..YEAR_MAX
    }

    companion object {
        private const val PARAM_PRIMARY_RELEASE_YEAR = "primary_release_year"
        private const val PARAM_VOTE_COUNT_GTE = "vote_count.gte"
        private const val PARAM_VOTE_AVERAGE_GTE = "vote_average.gte"
        private const val PARAM_WITH_GENRES = "with_genres"
        private const val PARAM_WITH_KEYWORDS = "with_keywords"
        private const val PARAM_RELEASE_DATE_GTE = "release_date.gte"
        private const val PARAM_RELEASE_DATE_LTE = "release_date.lte"
        private const val PARAM_CERTIFICATION_COUNTRY = "certification_country"
        private const val PARAM_CERTIFICATION_LTE = "certification.lte"
        private const val PARAM_WITH_COMPANIES = "with_companies"
        private const val PARAM_SORT_BY = "sort_by"
        private const val YEAR_MIN = 1900
        private const val YEAR_MAX = 2100

        const val SORT_BY_ORIGINAL_TITLE_ASC = "original_title.asc"
        const val SORT_BY_ORIGINAL_TITLE_DSC = "original_title.desc"
        const val SORT_BY_POP_ASC = "popularity.asc"
        const val SORT_BY_POP_DES = "popularity.desc"
        const val SORT_BY_PRIMARY_RELEASE_DATE_ASC = "primary_release_date.asc"
        const val SORT_BY_PRIMARY_RELEASE_DATE_DES = "primary_release_date.desc"
        const val SORT_BY_REL_ASC = "release_date.asc"
        const val SORT_BY_REL_DES = "release_date.desc"
        const val SORT_BY_REV_ASC = "revenue.asc"
        const val SORT_BY_REV_DES = "revenue.desc"
        const val SORT_BY_VOTE_AVG_ASC = "vote_average.asc"
        const val SORT_BY_VOTE_AVG_DES = "vote_average.desc"
        const val SORT_BY_VOTE_COUNT_ASC = "vote_count.asc"
        const val SORT_BY_VOTE_COUNT_DES = "vote_count.desc"
    }
}