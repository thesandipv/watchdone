package com.afterroot.tmdbapi;

import com.afterroot.tmdbapi.model.core.MovieResultsPage;
import com.afterroot.tmdbapi.model.core.ResultsPage;
import com.afterroot.tmdbapi.model.keywords.Keyword;
import com.afterroot.tmdbapi.tools.ApiUrl;


public class TmdbKeywords extends AbstractTmdbApi {

    public static final String TMDB_METHOD_KEYWORD = "keyword";


    TmdbKeywords(TmdbApi tmdbApi) {
        super(tmdbApi);
    }


    /**
     * Get the basic information for a specific keyword id.
     *
     * @param keywordId
     * @return
     */
    public Keyword getKeyword(String keywordId) {
        ApiUrl apiUrl = new ApiUrl(TMDB_METHOD_KEYWORD, keywordId);

        return mapJsonResult(apiUrl, Keyword.class);
    }


    /**
     * Get the list of movies for a particular keyword by id.
     *
     * @param keywordId
     * @param language
     * @param page
     * @return List of movies with the keyword
     */
    public MovieResultsPage getKeywordMovies(String keywordId, String language, Integer page) {
        ApiUrl apiUrl = new ApiUrl(TMDB_METHOD_KEYWORD, keywordId, "movies");

        apiUrl.addLanguage(language);

        apiUrl.addPage(page);

        return mapJsonResult(apiUrl, MovieResultsPage.class);
    }


    public static class KeywordResultsPage extends ResultsPage<Keyword> {

    }
}
