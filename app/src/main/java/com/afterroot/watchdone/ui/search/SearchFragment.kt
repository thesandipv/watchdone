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
import com.afterroot.watchdone.adapter.DelegateAdapter
import com.afterroot.watchdone.adapter.ItemSelectedCallback
import com.afterroot.watchdone.ui.home.HomeViewModel
import com.afterroot.watchdone.utils.hideKeyboard
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.toast
import org.koin.android.ext.android.get

class SearchFragment : Fragment() {
    private val homeViewModel: HomeViewModel by activityViewModels()
    private var queryTextListener: SearchView.OnQueryTextListener? = null
    private var searchResultsAdapter: DelegateAdapter? = null
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
        val itemSelectedCallback = object : ItemSelectedCallback<MovieDb> {
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
        searchResultsAdapter = DelegateAdapter(itemSelectedCallback)
        list.adapter = searchResultsAdapter
    }

    private fun showSearchResults(title: String) = lifecycleScope.launch(Dispatchers.Main) {
        progress_bar_search.visible(true, AutoTransition())
        list.visible(false, AutoTransition())
        try {
            val movies = withContext(Dispatchers.Default) { get<TmdbApi>().search.searchMovie(title) }
            progress_bar_search.visible(false, AutoTransition())
            list.visible(true, AutoTransition())
            searchResultsAdapter?.add(movies.results)
            list.scheduleLayoutAnimation()
        } catch (mde: MovieDbException) {
            mde.printStackTrace()
            progress_bar_search.visible(false)
        }
    }

    private fun loadTrending() = lifecycleScope.launch(Dispatchers.Main) {
        progress_bar_search.visible(true, AutoTransition())
        list.visible(false, AutoTransition())
        try {
            homeViewModel.getTrendingMovies().observe(viewLifecycleOwner, Observer {
                progress_bar_search.visible(false, AutoTransition())
                list.visible(true, AutoTransition())
                searchResultsAdapter?.add(it.results)
                list.scheduleLayoutAnimation()
            })
        } catch (mde: MovieDbException) {
            mde.printStackTrace()
            progress_bar_search.visible(false)
        }
    }

    private fun setErrorObserver() {
        homeViewModel.error.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                progress_bar_search.visible(false, AutoTransition())
                requireContext().toast("Via: $TAG : $it")
                //Set value to null after displaying error so prevent Observers from another context
                homeViewModel.error.postValue(null)
            }
        })
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