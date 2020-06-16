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

package com.afterroot.watchdone.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.transition.AutoTransition
import com.afterroot.core.extensions.visible
import com.afterroot.tmdbapi.model.MovieDb
import com.afterroot.watchdone.R
import com.afterroot.watchdone.adapter.DelegateListAdapter
import com.afterroot.watchdone.adapter.ItemSelectedCallback
import com.afterroot.watchdone.adapter.MovieDiffCallback
import com.afterroot.watchdone.data.model.Field
import com.afterroot.watchdone.data.model.toMovieDataHolder
import com.afterroot.watchdone.databinding.FragmentHomeBinding
import com.afterroot.watchdone.ui.settings.Settings
import com.afterroot.watchdone.utils.EventObserver
import com.afterroot.watchdone.utils.getMailBodyForFeedback
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
    private lateinit var homeScreenAdapter: DelegateListAdapter
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val settings: Settings by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        homeScreenAdapter = DelegateListAdapter(MovieDiffCallback(), object : ItemSelectedCallback<MovieDb> {
            override fun onClick(position: Int, view: View?, item: MovieDb) {
                super.onClick(position, view, item)
                homeViewModel.selectMovie(item)
                findNavController().navigate(R.id.toMovieInfo)
            }

            override fun onLongClick(position: Int, item: MovieDb) {
                super.onLongClick(position, item)
                requireContext().toast(item.title.toString())
            }
        })
        binding.list.adapter = homeScreenAdapter
        homeScreenAdapter.submitQuery(settings.queryDirection)
        homeViewModel.addGenres(viewLifecycleOwner)

        setErrorObserver()
        setUpChips()
    }

    var isWatchedChecked: Boolean = false
    lateinit var sortChip: Chip
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

    private fun DelegateListAdapter.submitQuery(
        direction: Query.Direction,
        isReload: Boolean = false,
        filterWatched: Boolean = false
    ) {
        homeViewModel.getWatchlistSnapshot(get<FirebaseAuth>().currentUser?.uid!!, isReload) {
            if (filterWatched) {
                whereEqualTo(Field.IS_WATCHED, filterWatched).orderBy(Field.RELEASE_DATE, direction)
            } else {
                orderBy(Field.RELEASE_DATE, direction)
            }
        }.observe(viewLifecycleOwner, Observer {
            if (it is ViewModelState.Loading) {
                binding.progressBarHome.visible(true)
            } else if (it is ViewModelState.Loaded<*>) {
                binding.progressBarHome.visible(false)
                try { //Fixes crash when user is being logged out
                    val listData = it.data as QuerySnapshot
                    submitList(listData.toMovieDataHolder())
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
                text = getMailBodyForFeedback()
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
