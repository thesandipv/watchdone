package com.afterroot.tmdbapi;

import com.afterroot.tmdbapi.model.ContentRating;
import com.afterroot.tmdbapi.model.Credits;
import com.afterroot.tmdbapi.model.MovieImages;
import com.afterroot.tmdbapi.model.config.Timezone;
import com.afterroot.tmdbapi.model.core.TvKeywords;
import com.afterroot.tmdbapi.model.tv.TvSeries;
import com.afterroot.tmdbapi.tools.ApiUrl;

import static com.afterroot.tmdbapi.Utils.asStringArray;


public class TmdbTV extends AbstractTmdbApi {

    public static final String TMDB_METHOD_TV = "tv";
    public static final String TMDB_METHOD_POPULAR = "popular";
    public static final String TMDB_METHOD_CREDITS = "credits";
    public static final String TMDB_METHOD_CONTENT_RATING = "content_rating";
    public static final String TMDB_METHOD_ONTHEAIR = "on_the_air";
    public static final String TMDB_METHOD_AIRINGTODAY = "airing_today";
    public static final String TMDB_METHOD_TOPRATED = "top_rated";
    public static final String TMDB_METHOD_RECOMMENDATIONS = "recommendations";
    public static final String TMDB_METHOD_KEYWORDS = "keywords";


    public enum TvMethod {credits, external_ids, images, videos, recommendations, keywords, content_ratings}



    TmdbTV(TmdbApi tmdbApi) {
        super(tmdbApi);
    }


    /**
     * This method is used to retrieve all of the basic series information.
     *
     * @param seriesId
     * @param language
     */
    public TvSeries getSeries(int seriesId, String language, TvMethod... appendToResponse) {
        ApiUrl apiUrl = new ApiUrl(TMDB_METHOD_TV, seriesId);

        apiUrl.addLanguage(language);

        apiUrl.appendToResponse(asStringArray(appendToResponse));

        return mapJsonResult(apiUrl, TvSeries.class);
    }


    public Credits getCredits(int seriesId, String language) {
        ApiUrl apiUrl = new ApiUrl(TMDB_METHOD_TV, seriesId, TMDB_METHOD_CREDITS);

        apiUrl.addLanguage(language);
        return mapJsonResult(apiUrl, Credits.class);
    }


    public TvResultsPage getPopular(String language, Integer page) {
        ApiUrl apiUrl = new ApiUrl(TMDB_METHOD_TV, TMDB_METHOD_POPULAR);

        apiUrl.addLanguage(language);

        apiUrl.addPage(page);

        return mapJsonResult(apiUrl, TvResultsPage.class);
    }


    public TvResultsPage getAiringToday(String language, Integer page, Timezone timezone) {
        ApiUrl apiUrl = new ApiUrl(TMDB_METHOD_TV, TMDB_METHOD_AIRINGTODAY);

        apiUrl.addLanguage(language);

        apiUrl.addPage(page);

        if (timezone != null) {
            apiUrl.addParam("timezone", timezone);
        }
        
        return mapJsonResult(apiUrl, TvResultsPage.class);
    }


    public TvResultsPage getOnTheAir(String language, Integer page) {
        ApiUrl apiUrl = new ApiUrl(TMDB_METHOD_TV, TMDB_METHOD_ONTHEAIR);

        apiUrl.addLanguage(language);

        apiUrl.addPage(page);

        return mapJsonResult(apiUrl, TvResultsPage.class);
    }


    public TvResultsPage getTopRated(String language, Integer page) {
        ApiUrl apiUrl = new ApiUrl(TMDB_METHOD_TV, TMDB_METHOD_TOPRATED);

        apiUrl.addLanguage(language);

        apiUrl.addPage(page);

        return mapJsonResult(apiUrl, TvResultsPage.class);
    }

    public MovieImages getImages(int seriesId, String language) {
        ApiUrl apiUrl = new ApiUrl(TMDB_METHOD_TV, seriesId, TvMethod.images);

        apiUrl.addLanguage(language);

        return mapJsonResult(apiUrl, MovieImages.class);
    }

    public TvKeywords getKeywords(int seriesId, String language) {
        ApiUrl apiUrl = new ApiUrl(TMDB_METHOD_TV, seriesId, TMDB_METHOD_KEYWORDS);

        apiUrl.addLanguage(language);

        return mapJsonResult(apiUrl, TvKeywords.class);
    }
    
    public ContentRating.Results getContentRating(int seriesId, String language) {
    	ApiUrl apiUrl = new ApiUrl(TMDB_METHOD_TV, seriesId, TMDB_METHOD_CONTENT_RATING);

        apiUrl.addLanguage(language);
        
        return mapJsonResult(apiUrl, ContentRating.Results.class);
    }
}
