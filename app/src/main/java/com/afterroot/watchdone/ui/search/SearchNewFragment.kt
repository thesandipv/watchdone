/*
 * Copyright (C) 2020 Sandip Vaghela
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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.transition.AutoTransition
import com.afterroot.core.extensions.visible
import com.afterroot.tmdbapi.TmdbPeople
import com.afterroot.tmdbapi.TvResultsPage
import com.afterroot.tmdbapi.model.core.MovieResultsPage
import com.afterroot.watchdone.R
import com.afterroot.watchdone.adapter.SearchMoviesListAdapter
import com.afterroot.watchdone.adapter.SearchPeopleListAdapter
import com.afterroot.watchdone.adapter.SearchTVListAdapter
import com.afterroot.watchdone.adapter.delegate.ItemSelectedCallback
import com.afterroot.watchdone.data.movie.MovieDataHolder
import com.afterroot.watchdone.data.movie.toMovieDataHolder
import com.afterroot.watchdone.data.people.PeopleDataHolder
import com.afterroot.watchdone.data.people.toPeopleDataHolder
import com.afterroot.watchdone.data.tv.TVDataHolder
import com.afterroot.watchdone.data.tv.toTVDataHolder
import com.afterroot.watchdone.databinding.SearchNewFragmentBinding
import com.afterroot.watchdone.utils.hideKeyboard
import com.afterroot.watchdone.utils.showKeyboard
import com.afterroot.watchdone.viewmodel.HomeViewModel
import com.afterroot.watchdone.viewmodel.ViewModelState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchNewFragment : Fragment() {

    private val viewModel: SearchNewViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var binding: SearchNewFragmentBinding
    private var queryTextListener: SearchView.OnQueryTextListener? = null
    private var searchTask: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        binding = SearchNewFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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
        val searchView = binding.searchView
        searchView.setSearchableInfo(manager.getSearchableInfo(requireActivity().componentName))
        requireContext().showKeyboard(searchView)
        queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                Log.i("onQueryTextChange", newText)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                Log.i("onQueryTextSubmit", query)
                searchTask = showSearchResults(query)
                view?.let { requireContext().hideKeyboard(it) }
                return true
            }
        }
        searchView.setOnQueryTextListener(queryTextListener)
        searchView.setOnCloseListener {
            hideAll()
            return@setOnCloseListener true
        }
    }

    private fun showSearchResults(query: String): Job? {
        showMovies(true, isLoading = true)
        showTV(true, isLoading = true)
        showPeople(true, isLoading = true)
        return lifecycleScope.launch {
            val moviesListAdapter = SearchMoviesListAdapter(movieItemSelectedCallback)
            viewModel.searchMovies(query).observe(viewLifecycleOwner, Observer {
                if (it is ViewModelState.Loaded<*>) {
                    val data = it.data as MovieResultsPage
                    moviesListAdapter.submitList(data.toMovieDataHolder())
                    binding.moviesList.adapter = moviesListAdapter
                    if (data.totalResults > 0) {
                        showMovies(true, isLoading = false)
                    } else {
                        showMovies(false, isLoading = false)
                    }
                }

            })

            val tvListAdapter = SearchTVListAdapter(tvItemSelectedCallback)
            viewModel.searchTV(query).observe(viewLifecycleOwner, Observer {
                if (it is ViewModelState.Loaded<*>) {
                    val data = it.data as TvResultsPage
                    tvListAdapter.submitList(data.toTVDataHolder())
                    binding.tvList.adapter = tvListAdapter
                    if (data.totalResults > 0) {
                        showTV(true, isLoading = false)
                    } else {
                        showTV(false, isLoading = false)
                    }
                }
            })

            val peopleListAdapter = SearchPeopleListAdapter(peopleItemSelectedCallback)
            viewModel.searchPeople(query).observe(viewLifecycleOwner, Observer {
                if (it is ViewModelState.Loaded<*>) {
                    val data = it.data as TmdbPeople.PersonResultsPage
                    peopleListAdapter.submitList(data.toPeopleDataHolder())
                    binding.peopleList.adapter = peopleListAdapter
                    if (data.totalResults > 0) {
                        showPeople(true, isLoading = false)
                    } else {
                        showPeople(false, isLoading = false)
                    }
                }
            })
        }
    }

    private fun showMovies(isShow: Boolean, isLoading: Boolean = true) {
        binding.apply {
            titleMovies.visible(isShow, AutoTransition())
            moviesList.visible(!isLoading, AutoTransition())
            moviesPb.visible(isLoading)
        }
    }

    private fun showTV(isShow: Boolean, isLoading: Boolean = true) {
        binding.apply {
            titleTv.visible(isShow, AutoTransition())
            tvList.visible(!isLoading, AutoTransition())
            tvPb.visible(isLoading)
        }
    }

    private fun showPeople(isShow: Boolean, isLoading: Boolean = true) {
        binding.apply {
            titlePeople.visible(isShow, AutoTransition())
            peopleList.visible(!isLoading, AutoTransition())
            peoplePb.visible(isLoading)
        }
    }

    private fun hideAll() {
        showMovies(false, isLoading = false)
        showTV(false, isLoading = false)
        showPeople(false, isLoading = false)
    }

    private val movieItemSelectedCallback = object : ItemSelectedCallback<MovieDataHolder> {
        override fun onClick(position: Int, view: View?, item: MovieDataHolder) {
            super.onClick(position, view, item)
            homeViewModel.selectMovie(item.data)
            view?.findNavController()?.navigate(R.id.searchNewToMovieInfo)
        }
    }

    private val tvItemSelectedCallback = object : ItemSelectedCallback<TVDataHolder> {
        override fun onClick(position: Int, view: View?, item: TVDataHolder) {
            super.onClick(position, view, item)
            homeViewModel.selectTVSeries(item.data)
            view?.findNavController()?.navigate(R.id.searchNewToTVInfo)
        }
    }

    private val peopleItemSelectedCallback = object : ItemSelectedCallback<PeopleDataHolder> {
    }
}