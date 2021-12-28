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
package com.afterroot.watchdone.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.afterroot.core.extensions.visible
import com.afterroot.tmdbapi.model.Discover
import com.afterroot.tmdbapi.model.Multi
import com.afterroot.tmdbapi2.api.DiscoverApi
import com.afterroot.tmdbapi2.repository.DiscoverRepository
import com.afterroot.watchdone.R
import com.afterroot.watchdone.adapter.delegate.DelegateListAdapter
import com.afterroot.watchdone.data.mapper.toMovies
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.databinding.FragmentDiscoverBinding
import com.afterroot.watchdone.diff.MovieDiffCallback
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.common.ItemSelectedCallback
import com.afterroot.watchdone.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.jetbrains.anko.email
import org.jetbrains.anko.toast
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class DiscoverFragment : Fragment() {
    lateinit var binding: FragmentDiscoverBinding
    private val homeViewModel: HomeViewModel by activityViewModels()
    @Inject @Named("feedback_body") lateinit var feedbackBody: String
    @Inject lateinit var discoverApi: DiscoverApi
    @Inject lateinit var settings: Settings

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        binding = FragmentDiscoverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            binding.progressBarDiscover.visible(true)
            val repo = DiscoverRepository(discoverApi).getMoviesDiscover(Discover())
            val homeScreenAdapter = DelegateListAdapter(
                settings,
                MovieDiffCallback(),
                object :
                    ItemSelectedCallback<Movie> {
                    override fun onClick(position: Int, view: View?, item: Movie) {
                        super.onClick(position, view, item)
                        // homeViewModel.selectMovie(item)
                        // findNavController().navigate(R.id.discoverToMovieInfo)
                        val directions = DiscoverFragmentDirections.discoverToMediaInfo(item.id, Multi.MediaType.MOVIE.name)
                        findNavController().navigate(directions)
                    }

                    override fun onLongClick(position: Int, item: Movie) {
                        super.onLongClick(position, item)
                        requireContext().toast(item.title.toString())
                    }
                }
            )
            binding.list.adapter = homeScreenAdapter
            homeScreenAdapter.submitList(repo.toMovies())
            binding.progressBarDiscover.visible(false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.send_feedback) {
            requireContext().email(
                email = "afterhasroot@gmail.com",
                subject = "Watchdone Feedback",
                text = feedbackBody
            )
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_discover, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {
        private const val TAG = "DiscoverFragment"
    }
}
