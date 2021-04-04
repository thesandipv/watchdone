/*
 * Copyright (C) 2020-2021 Sandip Vaghela
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.afterroot.watchdone.ui.search

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.afterroot.tmdbapi.TmdbPeople
import com.afterroot.tmdbapi.TvResultsPage
import com.afterroot.tmdbapi.model.core.MovieResultsPage
import com.afterroot.tmdbapi.model.people.Person
import com.afterroot.watchdone.R
import com.afterroot.watchdone.adapter.SearchMoviesListAdapter
import com.afterroot.watchdone.adapter.SearchPeopleListAdapter
import com.afterroot.watchdone.adapter.SearchTVListAdapter
import com.afterroot.watchdone.adapter.delegate.ItemSelectedCallback
import com.afterroot.watchdone.data.mapper.toMovies
import com.afterroot.watchdone.data.mapper.toPersons
import com.afterroot.watchdone.data.mapper.toTV
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.databinding.SearchNewFragmentBinding
import com.afterroot.watchdone.providers.RecentSearchSuggestionsProvider
import com.afterroot.watchdone.utils.hideKeyboard
import com.afterroot.watchdone.utils.showKeyboard
import com.afterroot.watchdone.view.SectionalListView
import com.afterroot.watchdone.viewmodel.HomeViewModel
import com.afterroot.watchdone.viewmodel.ViewModelState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchNewFragment : Fragment() {

    private lateinit var binding: SearchNewFragmentBinding
    private lateinit var moviesSection: SectionalListView
    private lateinit var peopleSection: SectionalListView
    private lateinit var tvSection: SectionalListView
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val viewModel: SearchNewViewModel by activityViewModels()
    private var searchTask: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        binding = SearchNewFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        initSearch()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("search", binding.searchView.query.toString())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.getString("search")?.let { showSearchResults(it) }
    }

    private fun initSearch() {
        val manager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding.searchView.apply {
            setSearchableInfo(manager.getSearchableInfo(requireActivity().componentName))
            requireContext().showKeyboard(this)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String): Boolean {
                    Log.i("onQueryTextChange", newText)
                    return true
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    Log.i("onQueryTextSubmit", query)
                    performSearch(query)
                    return true
                }
            })
            setOnCloseListener {
                hideAll()
                return@setOnCloseListener true
            }
            setOnSuggestionListener(object : SearchView.OnSuggestionListener {
                override fun onSuggestionSelect(position: Int): Boolean {

                    return false
                }

                override fun onSuggestionClick(position: Int): Boolean {
                    performSearch(query.toString())
                    return true
                }
            })
        }
    }

    fun performSearch(query: String) {
        SearchRecentSuggestions(
            requireContext(),
            RecentSearchSuggestionsProvider.AUTHORITY,
            RecentSearchSuggestionsProvider.MODE
        ).saveRecentQuery(query, null)
        searchTask = showSearchResults(query)
        view?.let { requireContext().hideKeyboard(it) }
    }

    private fun showSearchResults(query: String): Job = lifecycleScope.launch {
        // SectionViews
        moviesSection = SectionalListView(requireContext()).withTitle(getString(R.string.text_search_movies)).withLoading()
        tvSection = SectionalListView(requireContext()).withTitle(getString(R.string.text_search_tv)).withLoading()
        peopleSection = SectionalListView(requireContext()).withTitle(getString(R.string.text_search_people)).withLoading()

        // Adapters
        val moviesListAdapter = SearchMoviesListAdapter(movieItemSelectedCallback)
        val tvListAdapter = SearchTVListAdapter(tvItemSelectedCallback)
        val peopleListAdapter = SearchPeopleListAdapter(peopleItemSelectedCallback)

        // Add Children
        binding.contentSearch.apply {
            addSectionAt(0, moviesSection)
            addSectionAt(1, tvSection)
            addSectionAt(2, peopleSection)
        }

        viewModel.searchMovies(query).observe(
            viewLifecycleOwner,
            {
                if (it is ViewModelState.Loaded<*>) {
                    val data = it.data as MovieResultsPage
                    moviesListAdapter.submitList(data.toMovies())
                    moviesSection.setAdapter(moviesListAdapter)
                    if (data.totalResults > 0) {
                        moviesSection.isLoaded = true
                    } else {
                        moviesSection.noResults()
                    }
                }
            }
        )

        viewModel.searchTV(query).observe(
            viewLifecycleOwner,
            {
                if (it is ViewModelState.Loaded<*>) {
                    val data = it.data as TvResultsPage
                    tvListAdapter.submitList(data.toTV())
                    tvSection.setAdapter(tvListAdapter)
                    if (data.totalResults > 0) {
                        tvSection.isLoaded = true
                    } else {
                        tvSection.noResults()
                    }
                }
            }
        )

        viewModel.searchPeople(query).observe(
            viewLifecycleOwner,
            {
                if (it is ViewModelState.Loaded<*>) {
                    val data = it.data as TmdbPeople.PersonResultsPage
                    peopleListAdapter.submitList(data.toPersons())
                    peopleSection.setAdapter(peopleListAdapter)
                    if (data.totalResults > 0) {
                        peopleSection.isLoaded = true
                    } else {
                        peopleSection.noResults()
                    }
                }
            }
        )
    }

    private fun LinearLayoutCompat.addSectionAt(index: Int, section: SectionalListView) {
        if (this.getChildAt(index) == null) {
            this.addView(section, index)
        }
    }

    private fun hideAll() {
        moviesSection.hide()
        tvSection.hide()
        peopleSection.hide()
    }

    private val movieItemSelectedCallback = object : ItemSelectedCallback<Movie> {
        override fun onClick(position: Int, view: View?, item: Movie) {
            super.onClick(position, view, item)
            homeViewModel.selectMovie(item)
            view?.findNavController()?.navigate(R.id.searchNewToMovieInfo)
        }
    }

    private val tvItemSelectedCallback = object : ItemSelectedCallback<TV> {
        override fun onClick(position: Int, view: View?, item: TV) {
            super.onClick(position, view, item)
            homeViewModel.selectTVSeries(item)
            view?.findNavController()?.navigate(R.id.searchNewToTVInfo)
        }
    }

    private val peopleItemSelectedCallback = object : ItemSelectedCallback<Person> {
        // TODO Add Cast info screen
    }
}
