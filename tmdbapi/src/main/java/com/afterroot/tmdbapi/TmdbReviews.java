package com.afterroot.tmdbapi;

import com.afterroot.tmdbapi.model.Reviews;
import com.afterroot.tmdbapi.model.core.ResultsPage;
import com.afterroot.tmdbapi.tools.ApiUrl;

import static com.afterroot.tmdbapi.TmdbMovies.TMDB_METHOD_MOVIE;


public class TmdbReviews extends AbstractTmdbApi {

    TmdbReviews(TmdbApi tmdbApi) {
        super(tmdbApi);
    }


    public ReviewResultsPage getReviews(int movieId, String language, Integer page) {
        ApiUrl apiUrl = new ApiUrl(TMDB_METHOD_MOVIE, movieId, "reviews");

        apiUrl.addLanguage(language);

        apiUrl.addPage(page);

        return mapJsonResult(apiUrl, ReviewResultsPage.class);
    }


    public static class ReviewResultsPage extends ResultsPage<Reviews> {

    }

}