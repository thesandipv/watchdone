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
package com.afterroot.watchdone.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.afollestad.materialdialogs.MaterialDialog
import com.afterroot.ui.common.compose.theme.Theme
import com.afterroot.utils.extensions.showStaticProgressDialog
import com.afterroot.watchdone.R
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.helpers.Deeplink
import com.afterroot.watchdone.helpers.migrateFirestore
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.common.ItemSelectedCallback
import com.afterroot.watchdone.watchlist.Watchlist
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.AndroidEntryPoint
import info.movito.themoviedbapi.model.Multi
import org.jetbrains.anko.toast
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class HomeFragment : Fragment() {
    @Inject lateinit var settings: Settings

    @Inject lateinit var firestore: FirebaseFirestore

    @Inject
    @Named("feedback_body")
    lateinit var feedbackBody: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Theme(context = requireContext(), settings = settings) {
                    Watchlist(itemSelectedCallback = itemSelectedCallback)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity() as MenuHost
        menuHost.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_main, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        else -> menuItem.onNavDestinationSelected(findNavController())
                    }
                }
            },
            viewLifecycleOwner
        )
    }

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

    /**
     * TODO Remove this check after 2 releases
     *
     * @param listData
     * @param userId
     * @since v0.0.4
     */
    private fun runMigrations(listData: QuerySnapshot, userId: String) {
        val isRunMig = listData.documents.any { doc ->
            doc.getDouble("voteAverage") != null
        }
        if (isRunMig) {
            MaterialDialog(requireContext()).show {
                title(text = "Migrate Data")
                message(text = "Migration Needed for new data structure")
                positiveButton(text = "Migrate") {
                    val progress = requireContext().showStaticProgressDialog("Migrating Data")
                    migrateFirestore(firestore, listData, userId, settings.isUseProdDb) {
                        progress.dismiss()
                    }
                }
                negativeButton(text = "Later")
            }
        }
    }
}
