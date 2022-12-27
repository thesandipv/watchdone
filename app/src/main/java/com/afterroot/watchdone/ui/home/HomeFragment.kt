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
package com.afterroot.watchdone.ui.home

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.transition.AutoTransition
import com.afollestad.materialdialogs.MaterialDialog
import com.afterroot.ui.common.compose.theme.Theme
import com.afterroot.ui.common.compose.utils.CenteredRow
import com.afterroot.utils.extensions.getDrawableExt
import com.afterroot.utils.extensions.showStaticProgressDialog
import com.afterroot.utils.extensions.visible
import com.afterroot.watchdone.R
import com.afterroot.watchdone.base.Field
import com.afterroot.watchdone.data.QueryAction
import com.afterroot.watchdone.data.mapper.toMulti
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.databinding.FragmentHomeBinding
import com.afterroot.watchdone.helpers.migrateFirestore
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.common.ItemSelectedCallback
import com.afterroot.watchdone.ui.media.adapter.MultiAdapter
import com.afterroot.watchdone.ui.media.adapter.MultiPagingAdapter
import com.afterroot.watchdone.viewmodel.EventObserver
import com.afterroot.watchdone.viewmodel.HomeViewModel
import com.afterroot.watchdone.viewmodel.ViewModelState
import com.afterroot.watchdone.watchlist.WatchlistActions
import com.afterroot.watchdone.watchlist.WatchlistViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.AndroidEntryPoint
import info.movito.themoviedbapi.model.Multi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import com.afterroot.watchdone.resources.R as CommonR

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeScreenPagingAdapter: MultiPagingAdapter
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val watchlistViewModel: WatchlistViewModel by viewModels()

    @Inject lateinit var settings: Settings

    @Inject lateinit var firebaseAuth: FirebaseAuth

    @Inject lateinit var firestore: FirebaseFirestore

    @Inject
    @Named("feedback_body")
    lateinit var feedbackBody: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val itemSelectedCallback = object : ItemSelectedCallback<Multi> {
        override fun onClick(position: Int, view: View?, item: Multi) {
            super.onClick(position, view, item)
            if (item is Movie) {
                val directions = HomeFragmentDirections.toMediaInfo(item.id, Multi.MediaType.MOVIE.name)
                if (findNavController().currentDestination?.id == R.id.navigation_home) {
                    findNavController().navigate(directions)
                }
            } else if (item is TV) {
                val directions = HomeFragmentDirections.toMediaInfo(item.id, Multi.MediaType.TV_SERIES.name)
                if (findNavController().currentDestination?.id == R.id.navigation_home) {
                    findNavController().navigate(directions)
                }
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

        homeScreenPagingAdapter = MultiPagingAdapter(itemSelectedCallback, settings)
        binding.list.adapter = homeScreenPagingAdapter
        // homeScreenAdapter.submitQuery(settings.queryDirection)
        homeViewModel.addGenres(viewLifecycleOwner)

        lifecycleScope.launch {
            watchlistViewModel.watchlist.collectLatest {
                homeScreenPagingAdapter.submitData(it)
            }
        }
        lifecycleScope.launch {
            watchlistViewModel.uiActions.collect { action ->
                when (action) {
                    WatchlistActions.Refresh -> {
                        Timber.d("UiAction: Refresh")
                        homeScreenPagingAdapter.refresh()
                    }
                    else -> {
                        // do nothing
                    }
                }
            }
        }

        setErrorObserver()
        setUpChips()
    }

    private var isWatchedChecked: Boolean = false
    private lateinit var sortChip: Chip
    private fun setUpChips() {
        // TODO Remember chip state using view model.
        sortChip = Chip(requireContext(), null, CommonR.attr.SortChipStyle).apply {
            text = if (settings.ascSort) "Ascending" else "Descending"
            chipIcon = requireContext().getDrawableExt(CommonR.drawable.ic_sort)
            setOnClickListener {
                val curr = settings.ascSort
                settings.ascSort = !curr
                this.text = if (!settings.ascSort) "Ascending" else "Descending"
                watchlistViewModel.submitAction(WatchlistActions.Refresh)
            }
        }

        val watchStatusGroup = ChipGroup(requireContext()).apply {
            isSingleSelection = true
            isSingleLine = true
        }

        val isWatchedChip = Chip(requireContext(), null, CommonR.attr.FilterChip).apply {
            text = context.getString(CommonR.string.text_ship_show_watched)
            isCheckable = true
            setOnCheckedChangeListener { _, isChecked ->
                watchlistViewModel.submitAction(WatchlistActions.SetQueryAction(if (isChecked) QueryAction.WATCHED else QueryAction.CLEAR))
                watchlistViewModel.submitAction(WatchlistActions.Refresh)
            }
        }

        val pending = Chip(requireContext(), null, CommonR.attr.FilterChip).apply {
            text = "Pending"
            isCheckable = true
            setOnCheckedChangeListener { _, isChecked ->
                watchlistViewModel.submitAction(WatchlistActions.SetQueryAction(if (isChecked) QueryAction.PENDING else QueryAction.CLEAR))
                watchlistViewModel.submitAction(WatchlistActions.Refresh)
            }
        }
        binding.chipGroup.apply {
            addView(sortChip)

            addView(
                ComposeView(requireContext()).apply {
                    gravity = Gravity.CENTER
                    layoutParams =
                        ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    setContent {
                        Theme(requireContext()) {
                            CenteredRow {
                                Spacer(modifier = Modifier.width(8.dp))
                                Divider(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(1.dp)
                                        .padding(vertical = 8.dp)
                                )
                                Icon(
                                    imageVector = Icons.Rounded.FilterAlt,
                                    contentDescription = "Filter Icon",
                                    modifier = Modifier.padding(8.dp),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            )

            watchStatusGroup.apply {
                addView(isWatchedChip)
                addView(pending)
            }
            addView(watchStatusGroup)
        }
    }

    // TODO Show loading
    fun setLoading(isLoading: Boolean) {
        binding.progressBarHome.visible(isLoading)
    }

    // TODO Show info message
    fun infoMessage(show: Boolean, action: QueryAction = QueryAction.CLEAR) {
        binding.infoNoMovies.visible(show, AutoTransition())
        binding.infoTv.text =
            if (action != QueryAction.CLEAR) {
                getString(CommonR.string.text_info_no_movies_in_filter)
            } else {
                getString(CommonR.string.text_info_no_movies)
            }
    }

    private fun MultiAdapter.submitQuery(
        direction: Query.Direction,
        isReload: Boolean = false,
        action: QueryAction = QueryAction.CLEAR,
        additionQueries: (Query.() -> Query)? = null
    ) {
        val userId = firebaseAuth.currentUser?.uid!!
        homeViewModel.getWatchlistSnapshot(userId, isReload) {
            additionQueries?.let { it() }
            when (action) {
                QueryAction.CLEAR -> {
                    orderBy(Field.RELEASE_DATE, direction)
                }
                QueryAction.WATCHED -> {
                    whereEqualTo(Field.IS_WATCHED, true).orderBy(Field.RELEASE_DATE, direction)
                }
                QueryAction.PENDING -> {
                    whereIn(Field.IS_WATCHED, listOf(false, null)).orderBy(Field.RELEASE_DATE, direction)
                }
            }
        }.observe(viewLifecycleOwner) {
            if (it is ViewModelState.Loading) {
                binding.progressBarHome.visible(true)
            } else if (it is ViewModelState.Loaded<*>) {
                binding.progressBarHome.visible(false)
                try { // Fixes crash when user is being logged out
                    if (it.data != null) {
                        binding.infoNoMovies.visible(false, AutoTransition())
                        val listData: QuerySnapshot = it.data as QuerySnapshot
                        if (listData.documents.isEmpty()) {
                            submitList(emptyList())
                            binding.infoNoMovies.visible(true, AutoTransition())
                            binding.infoTv.text =
                                if (action != QueryAction.CLEAR) {
                                    getString(CommonR.string.text_info_no_movies_in_filter)
                                } else {
                                    getString(CommonR.string.text_info_no_movies)
                                }
                        } else {
                            submitList(listData.toMulti())
                        }
                        // Check need migration or not
                        runMigrations(listData, userId)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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

    private fun setErrorObserver() {
        homeViewModel.error.observe(
            viewLifecycleOwner,
            EventObserver {
                binding.progressBarHome.visible(false, AutoTransition())
                requireContext().toast("Via: $TAG : $it")
            }
        )
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}
