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
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.afterroot.tmdbapi.model.Discover
import com.afterroot.tmdbapi.model.MovieDb
import com.afterroot.tmdbapi2.repository.DiscoverRepository
import com.afterroot.watchdone.R
import com.afterroot.watchdone.adapter.DelegateListAdapter
import com.afterroot.watchdone.adapter.ItemSelectedCallback
import com.afterroot.watchdone.adapter.MovieDiffCallback
import com.afterroot.watchdone.data.model.toMovieDataHolder
import com.afterroot.watchdone.databinding.FragmentDiscoverBinding
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.android.ext.android.get

class DiscoverFragment : Fragment() {
    lateinit var binding: FragmentDiscoverBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDiscoverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        lifecycleScope.launch {
            val repo = DiscoverRepository(get()).getMoviesDiscover(Discover())
            val homeScreenAdapter = DelegateListAdapter(MovieDiffCallback(), object : ItemSelectedCallback<MovieDb> {
                override fun onClick(position: Int, view: View?, item: MovieDb) {
                    super.onClick(position, view, item)
                    homeViewModel.selectMovie(item)
                    findNavController().navigate(R.id.discoverToMovieInfo)
                }

                override fun onLongClick(position: Int, item: MovieDb) {
                    super.onLongClick(position, item)
                    requireContext().toast(item.title.toString())
                }
            })
            binding.list.adapter = homeScreenAdapter
            homeScreenAdapter.submitList(repo.toMovieDataHolder())
        }
    }

    companion object {
        private const val TAG = "DiscoverFragment"
    }
}