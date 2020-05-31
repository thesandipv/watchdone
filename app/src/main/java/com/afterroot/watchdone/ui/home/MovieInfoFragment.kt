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

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.afterroot.tmdbapi.model.MovieDb
import com.afterroot.watchdone.R
import com.afterroot.watchdone.database.DatabaseFields
import com.afterroot.watchdone.database.MyDatabase
import com.afterroot.watchdone.databinding.FragmentMovieInfoBinding
import com.afterroot.watchdone.ui.settings.Settings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class MovieInfoFragment : Fragment() {
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val settings: Settings by inject()
    lateinit var binding: FragmentMovieInfoBinding
    private val myDatabase: MyDatabase by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMovieInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.getWatchlistSnapshot(get<FirebaseAuth>().currentUser?.uid!!)
            .observe(viewLifecycleOwner, Observer { state: ViewModelState? ->
                if (state is ViewModelState.Loaded<*>) {
                    val snapshot = state.data as QuerySnapshot
                    val moviesList = snapshot.toObjects(MovieDb::class.java)
                    homeViewModel.selectedMovie.observe(viewLifecycleOwner, Observer { movieDb: MovieDb ->
                        binding.apply {
                            moviedb = movieDb
                            movieDb.genreIds?.let {
                                myDatabase.genreDao().getGenres(it).observe(viewLifecycleOwner, Observer { roomGenres ->
                                    genres = roomGenres
                                })
                            }
                            settings = this@MovieInfoFragment.settings
                            val isInWatchlist = moviesList.contains(movieDb)
                            actionAddWlist.apply {
                                text =
                                    if (!isInWatchlist) getString(R.string.text_add_to_watchlist)
                                    else getString(R.string.text_remove_from_watchlist)
                                if (!isInWatchlist) {
                                    setOnClickListener {
                                        get<FirebaseFirestore>().collection(DatabaseFields.COLLECTION_USERS)
                                            .document(get<FirebaseAuth>().currentUser?.uid.toString())
                                            .collection(DatabaseFields.COLLECTION_WATCHDONE)
                                            .document(DatabaseFields.COLLECTION_WATCHLIST)
                                            .collection(DatabaseFields.COLLECTION_ITEMS)
                                            .document()
                                            .set(movieDb)
                                    }
                                } else {
                                    setOnClickListener {
                                        Log.d(TAG, "idOfMovie: ${snapshot.documents[moviesList.indexOf(movieDb)].id}")
                                    }
                                }
                            }
                            actionMarkWatched.setOnClickListener {

                            }
                        }
                    })
                }
            })
    }

    companion object {
        private const val TAG = "MovieInfoFragment"
    }
}
