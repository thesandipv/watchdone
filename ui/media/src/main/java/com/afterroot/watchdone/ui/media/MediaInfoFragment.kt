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

package com.afterroot.watchdone.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.findNavController
import com.afterroot.ui.common.compose.components.LocalPosterSize
import com.afterroot.ui.common.compose.theme.Theme
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.helpers.Deeplink
import com.afterroot.watchdone.settings.Settings
import dagger.hilt.android.AndroidEntryPoint
import info.movito.themoviedbapi.model.Multi
import org.jetbrains.anko.browse
import javax.inject.Inject

@AndroidEntryPoint
class MediaInfoFragment : Fragment() {

    @Inject lateinit var settings: Settings

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Theme(context = requireContext(), settings = settings) {
                    MediaInfoContent(navigateUp = findNavController()::navigateUp, onRecommendedClick = {
                        if (it is Movie) {
                            val request = NavDeepLinkRequest.Builder
                                .fromUri(Deeplink.media(it.id, it.mediaType))
                                .build()
                            findNavController().navigate(request)
                        } else if (it is TV) {
                            val request = NavDeepLinkRequest.Builder
                                .fromUri(Deeplink.media(it.id, it.mediaType))
                                .build()
                            findNavController().navigate(request)
                        }
                    }, onWatchProviderClick = { link ->
                        requireContext().browse(link, true)
                    })
                }
            }
        }
    }

    @Composable
    fun MediaInfoContent(
        navigateUp: () -> Unit,
        onRecommendedClick: (media: Multi) -> Unit,
        onWatchProviderClick: (link: String) -> Unit = { _ -> }
    ) {
        CompositionLocalProvider(
            LocalPosterSize provides (
                this@MediaInfoFragment.settings.imageSize
                    ?: this@MediaInfoFragment.settings.defaultImagesSize
                )
        ) {
            MediaInfo(
                navigateUp = navigateUp,
                onRecommendedClick = onRecommendedClick,
                onWatchProviderClick = onWatchProviderClick
            )
        }
    }
}
