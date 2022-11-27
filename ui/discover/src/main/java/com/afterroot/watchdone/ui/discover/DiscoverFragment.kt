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
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.afterroot.tmdbapi.api.DiscoverApi
import com.afterroot.ui.common.compose.theme.Theme
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.helpers.Deeplink
import com.afterroot.watchdone.media.adapter.MultiAdapter
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.common.ItemSelectedCallback
import com.afterroot.watchdone.ui.discover.databinding.FragmentDiscoverBinding
import com.afterroot.watchdone.viewmodel.DiscoverViewModel
import com.afterroot.watchdone.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import info.movito.themoviedbapi.model.Multi
import org.jetbrains.anko.toast
import javax.inject.Inject

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
                    .fromUri(Deeplink.media(item.id, Multi.MediaType.MOVIE))
                    .build()
                findNavController().navigate(request)
            } else if (item is TV) {
                val request = NavDeepLinkRequest.Builder
                    .fromUri(Deeplink.media(item.id, Multi.MediaType.TV_SERIES))
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
        return ComposeView(requireContext()).apply {
            setContent {
                Theme(context = requireContext()) {
                    Discover(itemSelectedCallback = itemSelectedCallback)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
/*
        discoverAdapter = MultiAdapter(itemSelectedCallback, settings)

        discoverViewModel.submitAction(DiscoverActions.SetMediaType(Multi.MediaType.MOVIE))

        lifecycleScope.launch {
            binding.progressBarDiscover.visible(true)
            binding.list.adapter = discoverAdapter
            binding.progressBarDiscover.visible(false)
            discoverViewModel.getMedia().collect {
                discoverAdapter.submitList(it)
            }
        }

        binding.composeViewChip.setContent {
            Theme(requireContext()) {
                DiscoverChips(discoverViewModel = discoverViewModel)
            }
        }*/
    }

    companion object {
        private const val TAG = "DiscoverFragment"
    }
}
