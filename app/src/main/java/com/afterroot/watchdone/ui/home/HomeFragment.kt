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
import com.afterroot.watchdone.utils.EventObserver
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import org.jetbrains.anko.toast
import org.koin.android.ext.android.get

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val homeScreenAdapter = DelegateListAdapter(MovieDiffCallback(), object : ItemSelectedCallback<MovieDb> {
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
        homeScreenAdapter.submitQuery(Query.Direction.DESCENDING)
        homeViewModel.addGenres(viewLifecycleOwner)

        val queryMap = hashMapOf<String, Any>()
        queryMap["Ascending"] = Query.Direction.ASCENDING
        queryMap["Descending"] = Query.Direction.DESCENDING

        queryMap.forEach { mapItem ->
            val chip = Chip(requireContext())
            chip.text = mapItem.key
            chip.setOnClickListener {
                homeScreenAdapter.submitQuery(mapItem.value as Query.Direction, true)
            }
            binding.chipGroup.addView(chip)
        }

        setErrorObserver()
    }

    private fun DelegateListAdapter.submitQuery(direction: Query.Direction, isReload: Boolean = false) {
        homeViewModel.getWatchlistSnapshot(get<FirebaseAuth>().currentUser?.uid!!, isReload) {
            orderBy(Field.RELEASE_DATE, direction)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}
