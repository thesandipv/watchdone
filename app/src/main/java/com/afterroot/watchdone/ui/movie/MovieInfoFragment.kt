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

package com.afterroot.watchdone.ui.movie

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.transition.AutoTransition
import com.afterroot.core.extensions.getDrawableExt
import com.afterroot.core.extensions.visible
import com.afterroot.tmdbapi.model.MovieDb
import com.afterroot.tmdbapi2.model.MovieAppendableResponses
import com.afterroot.tmdbapi2.repository.MoviesRepository
import com.afterroot.watchdone.R
import com.afterroot.watchdone.adapter.CastListAdapter
import com.afterroot.watchdone.data.model.Collection
import com.afterroot.watchdone.data.model.Field
import com.afterroot.watchdone.data.model.toCastDataHolder
import com.afterroot.watchdone.database.MyDatabase
import com.afterroot.watchdone.databinding.FragmentMovieInfoBinding
import com.afterroot.watchdone.ui.settings.Settings
import com.afterroot.watchdone.utils.getMailBodyForFeedback
import com.afterroot.watchdone.viewmodel.HomeViewModel
import com.afterroot.watchdone.viewmodel.ViewModelState
import com.google.android.gms.ads.AdRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.getField
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import org.jetbrains.anko.browse
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.email
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class MovieInfoFragment : Fragment() {
    private lateinit var binding: FragmentMovieInfoBinding
    private var menu: Menu? = null
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val myDatabase: MyDatabase by inject()
    private val settings: Settings by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        binding = FragmentMovieInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        homeViewModel.getWatchlistSnapshot(get<FirebaseAuth>().currentUser?.uid!!)
            .observe(viewLifecycleOwner, Observer { state: ViewModelState? ->
                if (state is ViewModelState.Loaded<*>) {
                    homeViewModel.selectedMovie.observe(viewLifecycleOwner, Observer { movieDb: MovieDb ->
                        updateUI(movieDb)
                    })
                }
            })

        setErrorObserver()

        binding.adView.loadAd(AdRequest.Builder().build())
    }

    private fun updateUI(movieDb: MovieDb) {
        val watchListRef = get<FirebaseFirestore>().collection(Collection.USERS)
            .document(get<FirebaseAuth>().currentUser?.uid.toString())
            .collection(Collection.WATCHDONE)
            .document(Collection.WATCHLIST)
        val watchlistItemReference = watchListRef.collection(Collection.ITEMS)
        binding.apply {
            settings = this@MovieInfoFragment.settings
            moviedb = movieDb
            updateGenres(movieDb)
            updateCast(movieDb)
            watchlistItemReference.whereEqualTo(Field.ID, movieDb.id).get().addOnSuccessListener {
                kotlin.runCatching { //Fix crash if user quickly press back button just after navigation
                    val isInWatchlist = it.documents.size > 0
                    var isWatched = false
                    var resultFromDB: MovieDb? = null
                    var selectedMovieDocId: String? = null
                    if (isInWatchlist) {
                        val document = it.documents[0]
                        document.getBoolean(Field.IS_WATCHED)?.let { watched ->
                            isWatched = watched
                        }
                        resultFromDB = document.toObject(MovieDb::class.java) as MovieDb
                        selectedMovieDocId = document.id
                        doShowingProgress {
                            lifecycleScope.launch {
                                val resultFromServer = getInfoFromServer(movieDb.id)
                                if (resultFromServer != resultFromDB) {
                                    Log.d(TAG, "updateUI: Local and Server data difference detected. Init Merging..")
                                    val selectedMovieDocRef = watchlistItemReference.document(selectedMovieDocId)
                                    selectedMovieDocRef.set(resultFromServer, SetOptions.merge()).addOnSuccessListener {
                                        Log.d(TAG, "updateUI: DB updated with latest data")
                                        hideProgress()
                                    }
                                    moviedb = resultFromServer
                                    updateGenres(resultFromServer)
                                    updateCast(resultFromServer)
                                } else {
                                    hideProgress()
                                    Log.d(TAG, "updateUI: Local and Server data almost same. Doing nothing.")
                                }
                            }
                        }
                    } else {
                        doShowingProgress {
                            lifecycleScope.launch {
                                moviedb = getInfoFromServer(movieDb.id)
                                updateGenres(moviedb!!)
                                updateCast(moviedb!!)
                                hideProgress()
                            }
                        }
                    }
                    actionAddWlist.apply {
                        visible(true)
                        if (!isInWatchlist) {
                            text = getString(R.string.text_add_to_watchlist)
                            icon = requireContext().getDrawableExt(R.drawable.ic_bookmark_border)
                            setOnClickListener {
                                doShowingProgress {
                                    watchListRef.getTotalItemsCount { itemsCount ->
                                        if (itemsCount < 5) {
                                            watchlistItemReference.add(movieDb)
                                            watchListRef.updateTotalItemsCounter(1)
                                            snackBarMessage("Added to Watchlist")
                                            hideProgress()
                                        } else {
                                            snackBarMessage("Can't add more movies as limit of 5")
                                            hideProgress()
                                        }
                                    }
                                }

                            }
                        } else {
                            text = getString(R.string.text_remove_from_watchlist)
                            icon = requireContext().getDrawableExt(R.drawable.ic_bookmark)
                            setOnClickListener {
                                selectedMovieDocId?.let { id -> watchlistItemReference.document(id).delete() }
                                watchListRef.updateTotalItemsCounter(-1)
                                snackBarMessage("Removed from Watchlist")
                            }
                        }
                    }
                    actionMarkWatched.apply {
                        visible(true)
                        if (isInWatchlist && selectedMovieDocId != null && resultFromDB != null) {
                            if (isWatched) {
                                text = getString(R.string.text_mark_as_unwatched)
                                icon = requireContext().getDrawableExt(R.drawable.ic_clear)
                            } else {
                                text = getString(R.string.text_mark_as_watched)
                                icon = requireContext().getDrawableExt(R.drawable.ic_done)
                            }
                            setOnClickListener {
                                watchlistItemReference.document(selectedMovieDocId)
                                    .update(Field.IS_WATCHED, !isWatched)
                            }
                        } else {
                            setOnClickListener {
                                snackBarMessage("Please add to Watchlist First")
                            }
                        }
                    }
                }
            }
            menu?.findItem(R.id.action_view_imdb)?.isVisible = !binding.moviedb?.imdbId.isNullOrBlank()
        }
    }

    private suspend fun getInfoFromServer(id: Int) = withContext(Dispatchers.IO) {
        get<MoviesRepository>().getFullMovieInfo(id, MovieAppendableResponses.credits)
    }

    private fun DocumentReference.updateTotalItemsCounter(by: Long) {
        this.set(hashMapOf(Field.TOTAL_ITEMS to FieldValue.increment(by)), SetOptions.merge())
    }

    private fun DocumentReference.getTotalItemsCount(doOnSuccess: (Int) -> Unit) {
        this.get().addOnCompleteListener {
            if (it.result?.data != null) { //Fixes
                it.result?.getField<Int>(Field.TOTAL_ITEMS)?.let { items -> doOnSuccess(items) }
            } else {
                doOnSuccess(0)
            }
        }
    }

    private fun updateGenres(movieDb: MovieDb) {
        Log.d(TAG, "updateGenres: Updating Genres")
        if (movieDb.genres == null) {
            Log.d(TAG, "updateGenres: Map of Genres not available. Get data from GenresId")
            movieDb.genreIds?.let {
                myDatabase.genreDao().getGenres(it).observe(viewLifecycleOwner, Observer { roomGenres ->
                    binding.genres = roomGenres
                    Log.d(TAG, "updateGenres: Genres set from Local Database")
                })
            }
        } else {
            binding.genres = movieDb.genres
            Log.d(TAG, "updateGenres: Genres set from MovieDb Object")
        }
    }

    private fun updateCast(movieDb: MovieDb) {
        if (movieDb.getCast() != null) {
            val castAdapter = CastListAdapter()
            binding.castList.adapter = castAdapter
            castAdapter.submitList(movieDb.toCastDataHolder())
        }
    }

    private fun setErrorObserver() {
        homeViewModel.error.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                hideProgress()
                snackBarMessage("Error: $it")
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_view_imdb -> {
                binding.moviedb?.imdbId?.let {
                    val imdbUrl = HttpUrl.Builder().scheme("https")
                        .host("www.imdb.com")
                        .addPathSegments("title").addPathSegment(it).build()
                    requireContext().browse(imdbUrl.toUrl().toString(), true)
                }
            }
            R.id.send_feedback -> {
                requireContext().email(
                    email = "afterhasroot@gmail.com",
                    subject = "Watchdone Feedback",
                    text = getMailBodyForFeedback()
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_movie_info, menu)
        this.menu = menu
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun doShowingProgress(task: () -> Unit) {
        binding.progressMovieInfo.apply {
            if (visibility == View.GONE) {
                visible(true, AutoTransition())
            }
        }
        task()
    }

    private fun hideProgress() {
        binding.progressMovieInfo.apply {
            if (visibility == View.VISIBLE) {
                visible(false, AutoTransition())
            }
        }
    }

    private fun snackBarMessage(message: String) {
        binding.root.snackbar(message).anchorView = requireActivity().toolbar
    }

    companion object {
        private const val TAG = "MovieInfoFragment"
    }
}



