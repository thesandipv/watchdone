package com.afterroot.tmdbapi;

import com.afterroot.tmdbapi.model.tv.TvSeason;
import com.afterroot.tmdbapi.tools.ApiUrl;

import static com.afterroot.tmdbapi.TmdbTV.TMDB_METHOD_TV;
import static com.afterroot.tmdbapi.Utils.asStringArray;


public class TmdbTvSeasons extends AbstractTmdbApi {

    public static final String TMDB_METHOD_TV_SEASON = "season";


    public enum SeasonMethod {
        // base method shared by all tv apis
        credits, external_ids, images, videos
        // specific method for episodes
        // ...tbd
    }


    TmdbTvSeasons(TmdbApi tmdbApi) {
        super(tmdbApi);
    }


    public TvSeason getSeason(int seriesId, int seasonNumber, String language, SeasonMethod... appendToResponse) {
        ApiUrl apiUrl = new ApiUrl(TMDB_METHOD_TV, seriesId, TMDB_METHOD_TV_SEASON, seasonNumber);

        apiUrl.addLanguage(language);

        apiUrl.appendToResponse(asStringArray(appendToResponse));

        return mapJsonResult(apiUrl, TvSeason.class);
    }
}
