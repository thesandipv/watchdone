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

package com.afterroot.watchdone.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.transition.AutoTransition
import com.afterroot.core.extensions.visible
import com.afterroot.tmdbapi.model.MovieDb
import com.afterroot.tmdbapi.model.tv.TvSeries
import com.afterroot.watchdone.R
import com.afterroot.watchdone.adapter.MultiAdapter
import com.afterroot.watchdone.adapter.delegate.ItemSelectedCallback
import com.afterroot.watchdone.data.Field
import com.afterroot.watchdone.data.MultiDataHolder
import com.afterroot.watchdone.data.toMultiDataHolder
import com.afterroot.watchdone.databinding.FragmentHomeBinding
import com.afterroot.watchdone.ui.settings.Settings
import com.afterroot.watchdone.utils.getMailBodyForFeedback
import com.afterroot.watchdone.viewmodel.EventObserver
import com.afterroot.watchdone.viewmodel.HomeViewModel
import com.afterroot.watchdone.viewmodel.ViewModelState
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import org.jetbrains.anko.email
import org.jetbrains.anko.toast
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeScreenAdapter: MultiAdapter
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val settings: Settings by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val itemSelectedCallback = object : ItemSelectedCallback<MultiDataHolder> {
        override fun onClick(position: Int, view: View?, item: MultiDataHolder) {
            super.onClick(position, view, item)
            if (item.data is MovieDb) {
                homeViewModel.selectMovie(item.data as MovieDb)
                findNavController().navigate(
                    R.id.toMovieInfo,
                    null, null,
                    FragmentNavigatorExtras(
                        view?.findViewById<AppCompatImageView>(R.id.poster)!! to (item.data as MovieDb).title!!
                    )
                )
            } else if (item.data is TvSeries) {
                homeViewModel.selectTVSeries(item.data as TvSeries)
                findNavController().navigate(R.id.toTVInfo)
            }
        }

        override fun onLongClick(position: Int, item: MultiDataHolder) {
            super.onLongClick(position, item)
            if (item.data is MovieDb) {
                requireContext().toast((item.data as MovieDb).title.toString())
            } else if (item.data is TvSeries) {
                requireContext().toast((item.data as TvSeries).name.toString())
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        homeScreenAdapter = MultiAdapter(itemSelectedCallback)
        binding.list.adapter = homeScreenAdapter
        homeScreenAdapter.submitQuery(settings.queryDirection)
        homeViewModel.addGenres(viewLifecycleOwner)

        setErrorObserver()
        setUpChips()
    }

    private var isWatchedChecked: Boolean = false
    private lateinit var sortChip: Chip
    private fun setUpChips() {
        sortChip = Chip(requireContext(), null, R.attr.SortChipStyle).apply {
            text = if (settings.ascSort) "Sort by Ascending" else "Sort by Descending"
            setOnClickListener {
                val curr = settings.ascSort
                settings.ascSort = !curr
                this.text = if (!settings.ascSort) "Sort by Ascending" else "Sort by Descending"
                homeScreenAdapter.submitQuery(settings.queryDirection, true, isWatchedChecked)
            }
        }
        binding.chipGroup.addView(sortChip)

        val isWatchedChip = Chip(requireContext(), null, R.attr.FilterChip).apply {
            text = context.getString(R.string.text_ship_show_watched)
            setOnCheckedChangeListener { _, isChecked ->
                isWatchedChecked = isChecked
                homeScreenAdapter.submitQuery(settings.queryDirection, true, isWatchedChecked)
            }
        }
        binding.chipGroup.addView(isWatchedChip)
    }

    private fun MultiAdapter.submitQuery(
        direction: Query.Direction,
        isReload: Boolean = false,
        filterWatched: Boolean = false
    ) {
        homeViewModel.getWatchlistSnapshot(get<FirebaseAuth>().currentUser?.uid!!, isReload) {
            if (filterWatched) {
                whereEqualTo(Field.IS_WATCHED, filterWatched).orderBy(
                    Field.RELEASE_DATE, direction
                )
            } else {
                orderBy(Field.RELEASE_DATE, direction)
            }
        }.observe(viewLifecycleOwner, Observer {
            if (it is ViewModelState.Loading) {
                binding.progressBarHome.visible(true)
            } else if (it is ViewModelState.Loaded<*>) {
                binding.progressBarHome.visible(false)
                try { //Fixes crash when user is being logged out
                    if (it.data != null) {
                        binding.infoNoMovies.visible(false, AutoTransition())
                        val listData: QuerySnapshot = it.data as QuerySnapshot
                        if (listData.documents.isEmpty()) {
                            submitList(emptyList())
                            binding.infoNoMovies.visible(true, AutoTransition())
                            binding.infoTv.text = if (filterWatched) getString(R.string.text_info_no_movies_in_filter)
                            else getString(R.string.text_info_no_movies)
                        } else {
                            submitList(listData.toMultiDataHolder())
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun setErrorObserver() {
        homeViewModel.error.observe(viewLifecycleOwner, EventObserver {
            binding.progressBarHome.visible(false, AutoTransition())
            requireContext().toast("Via: $TAG : $it")
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.send_feedback) {
            requireContext().email(
                email = "afterhasroot@gmail.com",
                subject = "Watchdone Feedback",
                text = getMailBodyForFeedback(get())
            )
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}
