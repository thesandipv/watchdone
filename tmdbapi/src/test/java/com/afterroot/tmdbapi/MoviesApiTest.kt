package com.afterroot.tmdbapi

import com.afterroot.tmdbapi.Utils.createImageUrl
import com.afterroot.tmdbapi.model.MovieDb
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import java.lang.Boolean

class MoviesApiTest : AbstractTmdbApiTest() {
    @Test
    fun testGetMovieInfo() {
        val result = tmdb.movies.getMovie(ID_MOVIE_BLADE_RUNNER, LANGUAGE_ENGLISH)
        Assert.assertEquals("Incorrect movie information", "Blade Runner", result.originalTitle)
    }

    @Test
    fun testGetMovieInfoWithAppendedMethods() {
        val result = tmdb.movies.getMovie(ID_MOVIE_BLADE_RUNNER, LANGUAGE_ENGLISH, *TmdbMovies.MovieMethod.values())
        val productionCompany = result.productionCompanies?.get(0)
        Assert.assertEquals("Incorrect movie information", "Blade Runner", result.originalTitle)
        Assert.assertTrue("no videos", result.getVideos()!!.isNotEmpty())
        Assert.assertNotNull(result.getReleases())
        Assert.assertFalse(result.productionCompanies!!.isEmpty())
        Assert.assertNotNull(productionCompany?.logoPath)
        Assert.assertNotNull(productionCompany?.originCountry)
        Assert.assertNotNull(productionCompany?.name)
    }

    @Test
    fun testGetMovieAlternativeTitles() {
        var country = ""
        var result =
            tmdb.movies.getAlternativeTitles(ID_MOVIE_BLADE_RUNNER, country)
        Assert.assertTrue("No alternative titles found", result.size > 0)
        country = "US"
        result = tmdb.movies.getAlternativeTitles(ID_MOVIE_BLADE_RUNNER, country)
        Assert.assertTrue("No alternative titles found", result.size > 0)
    }

    @Test
    fun testGetMovieCasts() {
        val people = tmdb.movies.getCredits(ID_MOVIE_BLADE_RUNNER)
        Assert.assertTrue("No cast information", people.all.size > 0)
        val name1 = "Harrison Ford"
        val name2 = "Charles Knode"
        var foundName1 = Boolean.FALSE
        var foundName2 = Boolean.FALSE
        for (person in people.all) {
            if (!foundName1 && person.name.equals(name1, ignoreCase = true)) {
                foundName1 = Boolean.TRUE
            }
            if (!foundName2 && person.name.equals(name2, ignoreCase = true)) {
                foundName2 = Boolean.TRUE
            }
        }
        Assert.assertTrue("Couldn't find $name1", foundName1)
        Assert.assertTrue("Couldn't find $name2", foundName2)
    }

    @Test
    fun testGetMovieImages() {
        val language = ""
        val result =
            tmdb.movies.getImages(ID_MOVIE_BLADE_RUNNER, language)
        Assert.assertFalse("No artwork found", result.posters.isEmpty())
    }

    @Test
    fun testGetMovieKeywords() {
        val result =
            tmdb.movies.getKeywords(ID_MOVIE_BLADE_RUNNER)
        Assert.assertFalse("No keywords found", result.isEmpty())
    }

    @Test
    fun testMovieKeywordsWithHits() {
        val result = tmdb.movies.getKeywords(550)
        Assert.assertFalse("No keywords found", result.isEmpty())
    }

    @Test
    fun testMovieKeywordsAppendedMethod() {
//        List<Keyword> result = tmdb.getMovies().getKeywords(10191);
        val movie = tmdb.movies.getMovie(10191, "fr", TmdbMovies.MovieMethod.keywords)
        val result = movie.getKeywords()
        Assert.assertFalse("No keywords found", result!!.isEmpty())
        Assert.assertEquals(19, result.size.toLong())
    }

    @Test
    fun testGetMovieReleaseInfo() {
        val result =
            tmdb.movies.getReleaseInfo(ID_MOVIE_BLADE_RUNNER, "")
        Assert.assertFalse("Release information missing", result.isEmpty())
    }

    //    @Test
    //    public void testGetMovieTrailers() {
    //        List<Trailer> result = tmdb.getMovies().getTrailers(ID_MOVIE_BLADE_RUNNER, "");
    //        assertFalse("Movie trailers missing", result.isEmpty());
    //    }
    @Test
    fun testGetMovieVideos() {
        val result =
            tmdb.movies.getVideos(ID_MOVIE_BLADE_RUNNER, "")
        System.err.println(result)
        Assert.assertFalse("Movie trailers missing", result.isEmpty())
    }

    @Test
    fun testGetMovieTranslations() {
        val result =
            tmdb.movies.getTranslations(ID_MOVIE_BLADE_RUNNER)
        val translationData = result[0].data
        Assert.assertFalse("No translations found", result.isEmpty())
        Assert.assertNotNull(translationData)
        Assert.assertNotNull(translationData.title)
        Assert.assertNotNull(translationData.overview)
        Assert.assertNotNull(translationData.homepage)
    }

    @Test
    fun testCreateImageUrl() {
        val (_, _, _, _, _, _, _, _, _, _, _, _, posterPath) = tmdb.movies.getMovie(ID_MOVIE_BLADE_RUNNER, "")
        val result =
            createImageUrl(tmdb, posterPath, "original").toString()
        Assert.assertFalse("Error compiling image URL", result.isEmpty())
    }

    @Test
    fun testGetNowPlayingMovies() {
        val result: List<MovieDb?> =
            tmdb.movies.getNowPlayingMovies(LANGUAGE_DEFAULT, 0, null).results
        Assert.assertTrue("No now playing movies found", !result.isEmpty())
    }

    @Test
    fun testGetPopularMovieList() {
        val result: List<MovieDb?> =
            tmdb.movies.getPopularMovies(LANGUAGE_DEFAULT, 0).results
        Assert.assertTrue("No popular movies found", !result.isEmpty())
        Assert.assertNotNull(result[0]!!.originalTitle)
        //        assertNotNull(result.get(0).getImdbID());
    }

    @Test
    fun testGetTopRatedMovies() {
        val results: List<MovieDb?> =
            tmdb.movies.getTopRatedMovies(LANGUAGE_DEFAULT, 0).results
        Assert.assertTrue("No top rated movies found", !results.isEmpty())
    }

    @Test
    fun testGetSimilarMovies() {
        val result: List<MovieDb?> = tmdb.movies.getSimilarMovies(
            ID_MOVIE_BLADE_RUNNER,
            LANGUAGE_DEFAULT,
            0
        ).results
        Assert.assertTrue("No similar movies found", !result.isEmpty())
    }

    @Test
    fun testGetLatestMovie() {
        val result = tmdb.movies.latestMovie
        Assert.assertTrue("No latest movie found", result != null)
        Assert.assertTrue("No latest movie found", result!!.id > 0)
    }

    @Test
    @Throws(Exception::class)
    fun testGetUpcoming() {
        val result: List<MovieDb?> = tmdb.movies.getUpcoming("en-US", 1, null).results
        Assert.assertTrue("No upcoming movies found", !result.isEmpty())
    }

    @Ignore("Do not test this until it is fixed")
    @Throws(Exception::class)
    fun testGetMovieChanges() {
        val startDate = ""
        val endDate: String? = null

        // Get some popular movies
        val movieList: List<MovieDb?> =
            tmdb.movies.getPopularMovies(LANGUAGE_DEFAULT, 0).results
        for (movie in movieList) {
            val result = tmdb.movies.getChanges(movie!!.id, startDate, endDate)
            Assert.assertTrue("No changes found", result.changedItems.size > 0)
            break
        }
    }

    @Test
    fun testInvalidID() {
        try {
            tmdb.movies.getMovie(199392, "fr", *TmdbMovies.MovieMethod.values())
            Assert.fail("exception should have been thrown")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun testGetCredits() {
        val tmdbMovies = tmdb.movies
        // default response will not return movie credits
        var movieDb = tmdbMovies.getMovie(293660, "en")
        Assert.assertNull("No credits(cast/crew) returned", movieDb.credits)
        // call API requesting for credits
        // Request URL be like https://api.themoviedb.org/3/movie/293660?append_to_response=credits&language=en
        movieDb = tmdbMovies.getMovie(293660, "en", TmdbMovies.MovieMethod.credits)
        Assert.assertNotNull("Credits returned", movieDb.credits)
        Assert.assertTrue("Credits-cast found", movieDb.getCast()!!.isNotEmpty())
        Assert.assertTrue("Credits-crew found", movieDb.getCrew()!!.isNotEmpty())
    }
}