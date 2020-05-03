package com.afterroot.tmdbapi

import com.afterroot.tmdbapi.TmdbAccount.MovieListResultsPage
import com.afterroot.tmdbapi.TmdbPeople.PersonResultsPage
import com.afterroot.tmdbapi.model.Collection
import com.afterroot.tmdbapi.model.Company
import com.afterroot.tmdbapi.model.Multi
import com.afterroot.tmdbapi.model.core.MovieResultsPage
import com.afterroot.tmdbapi.model.core.ResultsPage
import com.afterroot.tmdbapi.model.keywords.Keyword
import com.afterroot.tmdbapi.tools.ApiUrl
import org.apache.commons.lang3.StringUtils

class TmdbSearch(tmdbApi: TmdbApi?) : AbstractTmdbApi(tmdbApi!!) {
    /**
     * Search Movies This is a good starting point to start finding movies on TMDb.
     *
     * @param query
     * @param searchYear   Limit the search to the provided year. Zero (0) will get all years
     * @param language     The language to include. Can be blank/null.
     * @param includeAdult true or false to include adult titles in the search
     * @param page         The page of results to return. 0 to get the default (first page)
     */
    fun searchMovie(
        query: String,
        searchYear: Int? = 0,
        language: String? = null,
        includeAdult: Boolean = false,
        page: Int? = 0
    ): MovieResultsPage {
        val apiUrl = ApiUrl(TMDB_METHOD_SEARCH, TmdbMovies.TMDB_METHOD_MOVIE)
        if (StringUtils.isBlank(query)) {
            throw RuntimeException("query must not be blank")
        }
        apiUrl.addParam(PARAM_QUERY, query)

        // optional parameters
        if (searchYear != null && searchYear > 0) {
            apiUrl.addParam(PARAM_YEAR, searchYear.toString())
        }
        if (language != null) {
            apiUrl.addLanguage(language)
        }
        apiUrl.addParam(PARAM_ADULT, java.lang.Boolean.toString(includeAdult))
        apiUrl.addPage(page)
        return mapJsonResult(apiUrl, MovieResultsPage::class.java)
    }

    /**
     * Search for TV shows by title.
     *
     * @param query
     * @param language The language to include. Can be blank/null.
     * @param page     The page of results to return. 0 to get the default (first page)
     */
    fun searchTv(query: String, language: String?, page: Int?): TvResultsPage {
        val apiUrl = ApiUrl(TMDB_METHOD_SEARCH, TmdbTV.TMDB_METHOD_TV)
        if (StringUtils.isBlank(query)) {
            throw RuntimeException("query must not be blank")
        }
        apiUrl.addParam(PARAM_QUERY, query)

        // optional parameters
        if (language != null) {
            apiUrl.addLanguage(language)
        }
        apiUrl.addPage(page)
        return mapJsonResult(apiUrl, TvResultsPage::class.java)
    }

    /**
     * Search for collections by name.
     *
     * @param query
     * @param language
     * @param page
     */
    fun searchCollection(
        query: String,
        language: String?,
        page: Int?
    ): CollectionResultsPage {
        val apiUrl = ApiUrl(TMDB_METHOD_SEARCH, TmdbCollections.TMDB_METHOD_COLLECTION)
        if (StringUtils.isNotBlank(query)) {
            apiUrl.addParam(PARAM_QUERY, query)
        }
        if (language != null) {
            apiUrl.addLanguage(language)
        }
        apiUrl.addPage(page)
        return mapJsonResult(
            apiUrl,
            CollectionResultsPage::class.java
        )
    }

    /**
     * This is a good starting point to start finding people on TMDb.
     *
     *
     * The idea is to be a quick and light method so you can iterate through people quickly.
     *
     * @param query
     * @param includeAdult
     * @param page
     */
    fun searchPerson(query: String, includeAdult: Boolean, page: Int?): PersonResultsPage {
        val apiUrl = ApiUrl(TMDB_METHOD_SEARCH, TmdbPeople.TMDB_METHOD_PERSON)
        apiUrl.addParam(PARAM_QUERY, query)
        apiUrl.addParam(PARAM_ADULT, includeAdult)
        apiUrl.addPage(page)
        return mapJsonResult(apiUrl, PersonResultsPage::class.java)
    }

    /**
     * Search for lists by name and description.
     *
     * @param query
     * @param language
     * @param page
     */
    fun searchList(query: String, language: String?, page: Int?): MovieListResultsPage {
        System.err.println(
            "This method is part of the API but seems currently not available. " +
                    "See https://www.themoviedb.org/talk/593409e3c3a36859ef01eddb#597124f8c3a3681608008424"
        )
        val apiUrl = ApiUrl(TMDB_METHOD_SEARCH, TmdbLists.TMDB_METHOD_LIST)
        if (StringUtils.isNotBlank(query)) {
            apiUrl.addParam(PARAM_QUERY, query)
        }
        if (language != null) {
            apiUrl.addLanguage(language)
        }
        apiUrl.addPage(page)
        return mapJsonResult(apiUrl, MovieListResultsPage::class.java)
    }

    /**
     * Search Companies.
     *
     *
     * You can use this method to search for production companies that are part of TMDb. The company IDs will map to
     * those returned on movie calls.
     *
     *
     * http://help.themoviedb.org/kb/api/search-companies
     *
     * @param companyName
     * @param page
     */
    fun searchCompany(companyName: String, page: Int?): CompanyResultsPage {
        val apiUrl = ApiUrl(TMDB_METHOD_SEARCH, "company")
        apiUrl.addParam(PARAM_QUERY, companyName)
        apiUrl.addPage(page)
        return mapJsonResult(apiUrl, CompanyResultsPage::class.java)
    }

    /**
     * Search for keywords by name
     *
     * @param query
     * @param page
     */
    fun searchKeyword(query: String, page: Int?): KeywordResultsPage {
        val apiUrl = ApiUrl(TMDB_METHOD_SEARCH, "keyword")
        if (StringUtils.isNotBlank(query)) {
            apiUrl.addParam(PARAM_QUERY, query)
        }
        apiUrl.addPage(page)
        return mapJsonResult(
            apiUrl,
            KeywordResultsPage::class.java
        )
    }

    /**
     * Search the movie, tv show and person collections with a single query.
     *
     * Each mapped result is the same response you would get from each independent search.
     * @param query
     * @param language
     * @param page
     * @return ResultsPage of Multi.
     * @see Multi
     */
    fun searchMulti(query: String, language: String?, page: Int?): MultiListResultsPage {
        val apiUrl = ApiUrl(TMDB_METHOD_SEARCH, TMDB_METHOD_MULTI)
        if (StringUtils.isBlank(query)) {
            throw RuntimeException("query must not be blank")
        }
        apiUrl.addParam(PARAM_QUERY, query)

        // optional parameters
        if (language != null) {
            apiUrl.addLanguage(language)
        }
        apiUrl.addPage(page)
        return mapJsonResult(apiUrl, MultiListResultsPage::class.java)
    }

    class KeywordResultsPage : ResultsPage<Keyword?>()
    class CompanyResultsPage : ResultsPage<Company?>()
    class CollectionResultsPage : ResultsPage<Collection?>()
    class MultiListResultsPage : ResultsPage<Multi?>()
    companion object {
        const val TMDB_METHOD_SEARCH = "search"
        private const val PARAM_QUERY = "query"
        val TMDB_METHOD_MULTI: Any = "multi"
    }
}