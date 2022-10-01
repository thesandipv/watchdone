/*
 * Copyright (C) 2020-2022 Sandip Vaghela
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
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.afterroot.tmdbapi.api.DiscoverApi
import com.afterroot.ui.common.compose.components.FilterChipGroup
import com.afterroot.ui.common.compose.theme.Theme
import com.afterroot.utils.extensions.visible
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.media.adapter.MultiAdapter
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.common.ItemSelectedCallback
import com.afterroot.watchdone.ui.discover.databinding.FragmentDiscoverBinding
import com.afterroot.watchdone.viewmodel.DiscoverActions
import com.afterroot.watchdone.viewmodel.DiscoverViewModel
import com.afterroot.watchdone.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import info.movito.themoviedbapi.model.Multi
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import javax.inject.Inject
import com.afterroot.watchdone.resources.R as CommonR

@AndroidEntryPoint
class DiscoverFragment : Fragment() {
    lateinit var binding: FragmentDiscoverBinding
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val discoverViewModel: DiscoverViewModel by viewModels()

    @Inject lateinit var discoverApi: DiscoverApi

    @Inject lateinit var settings: Settings
    private lateinit var discoverAdapter: MultiAdapter

    private val itemSelectedCallback = object : ItemSelectedCallback<Multi> {
        override fun onClick(position: Int, view: View?, item: Multi) {
            super.onClick(position, view, item)
            if (item is Movie) {
                val request = NavDeepLinkRequest.Builder
                    .fromUri("https://watchdone.web.app/media/${Multi.MediaType.MOVIE.name}/${item.id}".toUri())
                    .build()
                findNavController().navigate(request)
            } else if (item is TV) {
                val request = NavDeepLinkRequest.Builder
                    .fromUri("https://watchdone.web.app/media/${Multi.MediaType.TV_SERIES.name}/${item.id}".toUri())
                    .build()
                findNavController().navigate(request)
            }
        }

        override fun onLongClick(position: Int, item: Multi) {
            super.onLongClick(position, item)
            if (item is Movie) {
                requireContext().toast((item).title.toString())
            } else if (item is TV) {
                requireContext().toast((item).name.toString())
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDiscoverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        discoverAdapter = MultiAdapter(itemSelectedCallback, settings)

        discoverViewModel.submitAction(DiscoverActions.SetMediaType(Multi.MediaType.MOVIE))

        lifecycleScope.launch {
            binding.progressBarDiscover.visible(true)
            // val repo = DiscoverRepository(discoverApi).getMoviesDiscover(Discover())
/*
            val homeScreenAdapter = DelegateListAdapter(
                settings,
                MovieDiffCallback(),
                object :
                    ItemSelectedCallback<Movie> {
                    override fun onClick(position: Int, view: View?, item: Movie) {
                        super.onClick(position, view, item)
                        // homeViewModel.selectMovie(item)
                        // findNavController().navigate(R.id.discoverToMovieInfo)
                        val request = NavDeepLinkRequest.Builder
                            .fromUri("https://watchdone.web.app/media/${Multi.MediaType.MOVIE.name}/${item.id}".toUri())
                            .build()
                        // val directions = DiscoverFragmentDirections.discoverToMediaInfo(item.id, Multi.MediaType.MOVIE.name)
                        findNavController().navigate(request)
                    }

                    override fun onLongClick(position: Int, item: Movie) {
                        super.onLongClick(position, item)
                        requireContext().toast(item.title.toString())
                    }
                }
            )
*/
            binding.list.adapter = discoverAdapter
            // homeScreenAdapter.submitList(repo.toMovies())
            binding.progressBarDiscover.visible(false)
            discoverViewModel.getMedia().collect {
                discoverAdapter.submitList(it)
            }
        }

        binding.composeViewChip.setContent {
            Theme(requireContext()) {
                FilterChipGroup(
                    modifier = Modifier.padding(vertical = 8.dp),
                    chipSpacing = 12.dp,
                    horizontalPadding = dimensionResource(id = CommonR.dimen.padding_horizontal),
                    icons = listOf(Icons.Outlined.Movie, Icons.Outlined.Tv),
                    list = listOf("Movies", "TV"),
                    preSelect = listOf("Movies")
                ) { selected, _ ->
                    when (selected) {
                        "Movies" -> {
                            discoverViewModel.submitAction(DiscoverActions.SetMediaType(Multi.MediaType.MOVIE))
                        }
                        "TV" -> {
                            discoverViewModel.submitAction(DiscoverActions.SetMediaType(Multi.MediaType.TV_SERIES))
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "DiscoverFragment"
    }
}
