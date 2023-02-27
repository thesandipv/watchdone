/*
 * Copyright (C) 2020-2023 Sandip Vaghela
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
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.afterroot.ui.common.compose.theme.Theme
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.helpers.Deeplink
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.common.ItemSelectedCallback
import dagger.hilt.android.AndroidEntryPoint
import info.movito.themoviedbapi.model.Multi
import org.jetbrains.anko.toast
import javax.inject.Inject

@AndroidEntryPoint
class DiscoverFragment : Fragment() {

    @Inject lateinit var settings: Settings

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
        return ComposeView(requireContext()).apply {
            setContent {
                Theme(context = requireContext(), settings = settings) {
                    Discover(itemSelectedCallback = itemSelectedCallback)
                }
            }
        }
    }

    companion object {
        private const val TAG = "DiscoverFragment"
    }
}
