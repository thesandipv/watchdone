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
import androidx.navigation.fragment.findNavController
import androidx.transition.AutoTransition
import com.afollestad.materialdialogs.MaterialDialog
import com.afterroot.tmdbapi.model.Multi
import com.afterroot.utils.extensions.showStaticProgressDialog
import com.afterroot.utils.extensions.visible
import com.afterroot.watchdone.R
import com.afterroot.watchdone.base.Field
import com.afterroot.watchdone.data.mapper.toMulti
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.databinding.FragmentHomeBinding
import com.afterroot.watchdone.helpers.migrateFirestore
import com.afterroot.watchdone.media.adapter.MultiAdapter
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.common.ItemSelectedCallback
import com.afterroot.watchdone.viewmodel.EventObserver
import com.afterroot.watchdone.viewmodel.HomeViewModel
import com.afterroot.watchdone.viewmodel.ViewModelState
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.anko.email
import org.jetbrains.anko.toast
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeScreenAdapter: MultiAdapter
    private val homeViewModel: HomeViewModel by activityViewModels()
    @Inject lateinit var settings: Settings
    @Inject lateinit var firebaseAuth: FirebaseAuth
    @Inject lateinit var firestore: FirebaseFirestore
    @Inject @Named("feedback_body") lateinit var feedbackBody: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val itemSelectedCallback = object : ItemSelectedCallback<Multi> {
        override fun onClick(position: Int, view: View?, item: Multi) {
            super.onClick(position, view, item)
            if (item is Movie) {
                /*homeViewModel.selectMovie(item)
                findNavController().navigate(
                    R.id.toMovieInfo,
                    null, null,
                    FragmentNavigatorExtras(
                        view?.findViewById<AppCompatImageView>(R.id.poster)!! to (item).title!!
                    )
                )*/
                val directions = HomeFragmentDirections.toMediaInfo(item.id, Multi.MediaType.MOVIE.name)
                findNavController().navigate(directions)
            } else if (item is TV) {
                /*homeViewModel.selectTVSeries(item)
                findNavController().navigate(R.id.toTVInfo)*/
                val directions = HomeFragmentDirections.toMediaInfo(item.id, Multi.MediaType.TV_SERIES.name)
                findNavController().navigate(directions)
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

        homeScreenAdapter = MultiAdapter(itemSelectedCallback, settings)
        binding.list.adapter = homeScreenAdapter
        homeScreenAdapter.submitQuery(settings.queryDirection)
        homeViewModel.addGenres(viewLifecycleOwner)

        setErrorObserver()
        setUpChips()
    }

    private var isWatchedChecked: Boolean = false
    private lateinit var sortChip: Chip
    private fun setUpChips() {
        sortChip = Chip(requireContext(), null, R.attr.SortChipStyle).apply {
            text = if (settings.ascSort) "Sort by Ascending" else "Sort by Descending"
            setOnClickListener {
                val curr = settings.ascSort
                settings.ascSort = !curr
                this.text = if (!settings.ascSort) "Sort by Ascending" else "Sort by Descending"
                homeScreenAdapter.submitQuery(settings.queryDirection, true, QueryAction.CLEAR)
            }
        }

        val watchStatusGroup = ChipGroup(requireContext()).apply {
            isSingleSelection = true
        }

        val isWatchedChip = Chip(requireContext()).apply {
            text = context.getString(R.string.text_ship_show_watched)
            isCheckable = true
            setOnCheckedChangeListener { _, isChecked ->
                isWatchedChecked = isChecked
                if (isChecked) {
                    homeScreenAdapter.submitQuery(settings.queryDirection, isReload = true, action = QueryAction.WATCHED)
                } else homeScreenAdapter.clearFilters()
            }
        }

        val pending = Chip(requireContext()).apply {
            text = "Pending"
            isCheckable = true
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    homeScreenAdapter.submitList(
                        homeScreenAdapter.currentList.filter {
                            if (it is Movie) {
                                !it.isWatched
                            } else {
                                !(it as TV).isWatched
                            }
                        }
                    ) // homeScreenAdapter.submitQuery(settings.queryDirection, isReload = true, action = QueryAction.PENDING)
                } else homeScreenAdapter.clearFilters()
            }
        }
        binding.chipGroup.apply {
            addView(sortChip)

            watchStatusGroup.apply {
                addView(isWatchedChip)
                addView(pending)
            }
            addView(watchStatusGroup)
        }
    }

    enum class QueryAction {
        CLEAR,
        WATCHED,
        PENDING
    }

    private fun MultiAdapter.clearFilters() {
        submitQuery(settings.queryDirection, isReload = true)
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
                                if (action != QueryAction.CLEAR) getString(R.string.text_info_no_movies_in_filter)
                                else getString(R.string.text_info_no_movies)
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

    //TODO - Replace with MenuHost https://developer.android.com/jetpack/androidx/releases/activity#1.4.0-alpha01
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
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}
