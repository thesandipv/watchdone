package com.afterroot.tmdbapi;

import com.afterroot.tmdbapi.model.Collection;
import com.afterroot.tmdbapi.model.Company;
import com.afterroot.tmdbapi.model.NetworkMovie;
import com.afterroot.tmdbapi.model.MovieList;
import com.afterroot.tmdbapi.model.Multi;
import com.afterroot.tmdbapi.model.core.MovieResultsPage;
import com.afterroot.tmdbapi.model.keywords.Keyword;
import com.afterroot.tmdbapi.model.people.Person;
import com.afterroot.tmdbapi.model.tv.TvSeries;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class SearchApiTest extends AbstractTmdbApiTest {

    // fails currently but this is a known bug in tmdb http://www.themoviedb.org/talk/531e1f739251417a4a000bab
    @Test
    public void testQueryEscape() {
        TmdbSearch search = tmdb.getSearch();

        MovieResultsPage movieResultsPage = search.searchMovie("What Ever Happened to Baby Jane?", null, null, false, null);

        assertEquals(2, movieResultsPage.getResults().size());
        Assert.assertEquals(10242, movieResultsPage.getResults().get(0).getId());
    }


    @Test
    public void testSearchMovie() {
        // Try a movie with less than 1 page of results
        TmdbSearch search = tmdb.getSearch();

        List<NetworkMovie> movieList = search.searchMovie("Blade Runner", 0, "", true, 0).getResults();
//        List<NetworkMovie> movieList = tmdb.searchMovie("Blade Runner", "", true);
        assertTrue("No movies found, should be at least 1", movieList.size() > 0);

        // Try a russian language movie
        movieList = tmdb.getSearch().searchMovie("О чём говорят мужчины", 0, LANGUAGE_RUSSIAN, true, 0).getResults();
        assertTrue("No 'RU' movies found, should be at least 1", movieList.size() > 0);

        // Try a movie with more than 20 results
        movieList = tmdb.getSearch().searchMovie("Star Wars", 0, LANGUAGE_ENGLISH, false, 0).getResults();
        assertTrue("Not enough movies found, should be over 15, found " + movieList.size(), movieList.size() >= 15);
    }

    @Test
    public void testSearchTv() {
        List<TvSeries> result = tmdb.getSearch().searchTv("Breaking Bad", null, null).getResults();
        assertEquals("No series found", 1, result.size());
        assertEquals("Wrong series found", "Breaking Bad", result.get(0).getName());
        assertEquals("Wrong series found", 1396, result.get(0).getId());
    }


    @Test
    public void testSearchCollection() {
        List<Collection> result = tmdb.getSearch().searchCollection("batman", LANGUAGE_DEFAULT, 0).getResults();

        assertNotNull("No collections found", result);
        assertTrue("No collections found", result.size() > 0);
    }


    @Test
    public void testSearchPeople() {
        String personName = "Bruce Willis";
        boolean includeAdult = false;
        List<Person> result = tmdb.getSearch().searchPerson(personName, includeAdult, 0).getResults();
        assertTrue("Couldn't find the person", result.size() > 0);
    }


    @Test
    @Ignore("Part of api but somehow not yet implemented.")
    // Why ignored?
    // See https://www.themoviedb.org/talk/593409e3c3a36859ef01eddb#597124f8c3a3681608008424
    public void testSearchList() {
        List<MovieList> results = tmdb.getSearch().searchList("watch", LANGUAGE_DEFAULT, 0).getResults();

        assertFalse("No lists found", results == null);
        assertTrue("No lists found", results.size() > 0);
    }


    @Test
    public void testSearchCompanies() {
        List<Company> result = tmdb.getSearch().searchCompany(COMPANY_NAME, 0).getResults();
        assertTrue("No company information found", !result.isEmpty());
    }


    @Test
    public void testSearchKeyword() throws Exception {
        List<Keyword> results = tmdb.getSearch().searchKeyword("action", 0).getResults();

        assertFalse("No keywords found", results == null);
        assertTrue("No keywords found", results.size() > 0);
    }

    @Test
    public void testSearchMulti() throws Exception {
        List<Multi> results = tmdb.getSearch().searchMulti("dexter", null, null).getResults();

        assertFalse("No movie, person or tvseries found", results == null);
        assertTrue("No movie, person or tvseries found", results.size() > 0);
    }


}
