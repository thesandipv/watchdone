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
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.transition.AutoTransition
import com.afterroot.core.extensions.visible
import com.afterroot.tmdbapi.TmdbApi
import com.afterroot.tmdbapi.model.MovieDb
import com.afterroot.tmdbapi.tools.MovieDbException
import com.afterroot.watchdone.R
import com.afterroot.watchdone.adapter.delegate.DelegateListAdapter
import com.afterroot.watchdone.adapter.delegate.ItemSelectedCallback
import com.afterroot.watchdone.adapter.diff.MovieDiffCallback
import com.afterroot.watchdone.data.movie.toMovieDataHolder
import com.afterroot.watchdone.utils.getMailBodyForFeedback
import com.afterroot.watchdone.utils.hideKeyboard
import com.afterroot.watchdone.viewmodel.EventObserver
import com.afterroot.watchdone.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.email
import org.jetbrains.anko.toast
import org.koin.android.ext.android.get

class SearchFragment : Fragment() {
    private val homeViewModel: HomeViewModel by activityViewModels()
    private var queryTextListener: SearchView.OnQueryTextListener? = null
    private var searchResultsAdapter: DelegateListAdapter? = null
    private var searchTask: Job? = null
    private var searchView: SearchView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setErrorObserver()
        initAdapter()
        loadTrending()
    }

    override fun onDestroy() {
        super.onDestroy()
        searchTask?.apply {
            if (!isCompleted) cancel()
        }
    }

    private fun initAdapter() {
        val itemSelectedCallback = object :
            ItemSelectedCallback<MovieDb> {
            override fun onClick(position: Int, view: View?, item: MovieDb) {
                super.onClick(position, view, item)
                homeViewModel.selectMovie(item)
                findNavController().navigate(R.id.searchToMovieInfo)
            }

            override fun onLongClick(position: Int, item: MovieDb) {
                super.onLongClick(position, item)
                requireContext().toast(item.title.toString())
            }
        }
        searchResultsAdapter = DelegateListAdapter(
            MovieDiffCallback(),
            itemSelectedCallback
        )
        list.adapter = searchResultsAdapter
    }

    private fun showSearchResults(title: String) = lifecycleScope.launch(Dispatchers.Main) {
        progress_bar_search.visible(true, AutoTransition())
        list.visible(true, AutoTransition())
        requireActivity().title_bar_title.text = String.format(getString(R.string.format_search_result_for), title)
        try {
            val movies = withContext(Dispatchers.Default) { get<TmdbApi>().search.searchMovie(title) }
            progress_bar_search.visible(false, AutoTransition())
            searchResultsAdapter?.submitList(movies.toMovieDataHolder())
        } catch (mde: MovieDbException) {
            mde.printStackTrace()
            progress_bar_search.visible(false)
        }
    }

    private fun loadTrending() = lifecycleScope.launch(Dispatchers.Main) {
        progress_bar_search.visible(true, AutoTransition())
        list.visible(true, AutoTransition())
        try {
            homeViewModel.getTrendingMovies().observe(viewLifecycleOwner, Observer {
                progress_bar_search.visible(false, AutoTransition())
                searchResultsAdapter?.submitList(it.toMovieDataHolder())
            })
        } catch (mde: MovieDbException) {
            mde.printStackTrace()
            progress_bar_search.visible(false)
        }
    }

    private fun setErrorObserver() {
        homeViewModel.error.observe(viewLifecycleOwner, EventObserver {
            progress_bar_search.visible(false, AutoTransition())
            requireContext().toast("Via: $TAG : $it")
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.send_feedback) {
            requireContext().email(
                email = "afterhasroot@gmail.com",
                subject = "Watchdone Feedback",
                text = getMailBodyForFeedback()
            )
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        val search = menu.findItem(R.id.action_search)
        val manager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager

        if (search != null) {
            searchView = search.actionView as SearchView
        }

        if (searchView != null) {
            searchView!!.setSearchableInfo(manager.getSearchableInfo(requireActivity().componentName))
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
            searchView!!.setOnQueryTextListener(queryTextListener)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {
        private const val TAG = "SearchFragment"
    }
}