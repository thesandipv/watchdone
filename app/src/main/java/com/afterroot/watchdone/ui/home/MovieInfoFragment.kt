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
import com.afterroot.tmdbapi2.repository.MoviesRepository
import com.afterroot.watchdone.R
import com.afterroot.watchdone.database.MyDatabase
import com.afterroot.watchdone.database.model.Collection
import com.afterroot.watchdone.database.model.Field
import com.afterroot.watchdone.databinding.FragmentMovieInfoBinding
import com.afterroot.watchdone.ui.settings.Settings
import com.google.android.gms.ads.AdRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import org.jetbrains.anko.browse
import org.jetbrains.anko.toast
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
            watchlistItemReference.whereEqualTo(Field.ID, movieDb.id).get().addOnSuccessListener {
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
                    progressMovieInfo.visible(true, AutoTransition())
                    lifecycleScope.launch {
                        val resultFromServer =
                            withContext(Dispatchers.IO) { get<MoviesRepository>().getMovieInfo(movieDb.id) }
                        if (resultFromServer != resultFromDB) {
                            Log.d(TAG, "updateUI: Local and Server data difference detected. Init Merging..")
                            val selectedMovieDocRef = watchlistItemReference.document(selectedMovieDocId)
                            selectedMovieDocRef.set(resultFromServer, SetOptions.merge()).addOnSuccessListener {
                                Log.d(TAG, "updateUI: DB updated with latest data")
                                progressMovieInfo.visible(false, AutoTransition())
                            }
                            moviedb = resultFromServer
                            updateGenres(resultFromServer)
                        } else {
                            progressMovieInfo.visible(false, AutoTransition())
                            Log.d(TAG, "updateUI: Local and Server data almost same. Doing nothing.")
                        }
                    }
                } else {
                    progressMovieInfo.visible(true, AutoTransition())
                    lifecycleScope.launch {
                        val resultFromServer =
                            withContext(Dispatchers.IO) { get<MoviesRepository>().getMovieInfo(movieDb.id) }
                        moviedb = resultFromServer
                        progressMovieInfo.visible(false, AutoTransition())
                    }
                }
                actionAddWlist.apply {
                    visible(true)
                    if (!isInWatchlist) {
                        text = getString(R.string.text_add_to_watchlist)
                        icon = requireContext().getDrawableExt(R.drawable.ic_bookmark_border)
                        setOnClickListener {
                            watchlistItemReference.add(movieDb)
                            watchListRef.updateTotalItemsCounter(1)
                        }
                    } else {
                        text = getString(R.string.text_remove_from_watchlist)
                        icon = requireContext().getDrawableExt(R.drawable.ic_bookmark)
                        setOnClickListener {
                            selectedMovieDocId?.let { id -> watchlistItemReference.document(id).delete() }
                            watchListRef.updateTotalItemsCounter(-1)
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
                    }
                }
            }
            menu?.findItem(R.id.action_view_imdb)?.isVisible = !binding.moviedb?.imdbId.isNullOrBlank()
        }
    }

    private fun DocumentReference.updateTotalItemsCounter(by: Long) {
        this.set(hashMapOf(Field.TOTAL_ITEMS to FieldValue.increment(by)), SetOptions.merge())
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

    private fun setErrorObserver() {
        homeViewModel.error.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                progress_bar_search.visible(false, AutoTransition())
                requireContext().toast("Via: $TAG : $it")
                //Set value to null after displaying error so prevent Observers from another context
                homeViewModel.error.postValue(null)
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
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_movie_info, menu)
        this.menu = menu
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {
        private const val TAG = "MovieInfoFragment"
    }
}



