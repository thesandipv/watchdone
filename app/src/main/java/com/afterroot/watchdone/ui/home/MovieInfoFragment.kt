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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.afterroot.tmdbapi.model.MovieDb
import com.afterroot.watchdone.GlideApp
import com.afterroot.watchdone.R
import com.afterroot.watchdone.ui.settings.Settings
import com.afterroot.watchdone.database.DatabaseFields
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_movie_info.*
import org.jetbrains.anko.toast
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class MovieInfoFragment : Fragment() {
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val settings: Settings by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_movie_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.getWatchlistSnapshot(get<FirebaseAuth>().currentUser?.uid!!, get())
            .observe(viewLifecycleOwner, Observer { state: ViewModelState? ->
                if (state is ViewModelState.Loaded<*>) {
                    val snapshot = (state.data as QuerySnapshot).toObjects(MovieDb::class.java)
                    homeViewModel.selected.observe(viewLifecycleOwner, Observer { movieDb: MovieDb ->
                        GlideApp.with(this).load(settings.baseUrl + "w342" + movieDb.posterPath).into(movie_poster)
                        movie_title.text = movieDb.title
                        movie_overview.text = movieDb.overview
                        action_add_wlist.apply {
                            isEnabled = !snapshot.contains(movieDb)
                            setOnClickListener {
                                get<FirebaseFirestore>().collection(DatabaseFields.COLLECTION_USERS)
                                    .document(get<FirebaseAuth>().currentUser?.uid.toString())
                                    .collection(DatabaseFields.COLLECTION_WATCHDONE).document()
                                    .set(movieDb).addOnCompleteListener {
                                        requireContext().toast("Added to Watchlist")
                                    }
                            }
                        }
                        action_mark_watched.setOnClickListener {

                        }
                    })
                }
            })
    }

    companion object {
        private const val TAG = "MovieInfoFragment"
    }
}
