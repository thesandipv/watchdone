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
import androidx.navigation.fragment.findNavController
import androidx.transition.AutoTransition
import com.afterroot.core.extensions.visible
import com.afterroot.tmdbapi.model.MovieDb
import com.afterroot.watchdone.R
import com.afterroot.watchdone.adapter.DelegateAdapter
import com.afterroot.watchdone.adapter.ItemSelectedCallback
import com.afterroot.watchdone.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_search.*
import org.jetbrains.anko.toast
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin

class HomeFragment : Fragment() {
    private val homeViewModel: HomeViewModel by activityViewModels()
    lateinit var binding: FragmentHomeBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val homeScreenAdapter = DelegateAdapter(object : ItemSelectedCallback<MovieDb> {
            override fun onClick(position: Int, view: View?, item: MovieDb) {
                super.onClick(position, view, item)
                homeViewModel.selectMovie(item)
                findNavController().navigate(R.id.toMovieInfo)
            }

            override fun onLongClick(position: Int, item: MovieDb) {
                super.onLongClick(position, item)
                requireContext().toast(item.title.toString())
            }
        }, getKoin())
        binding.list.adapter = homeScreenAdapter
        homeViewModel.getWatchlistSnapshot(get<FirebaseAuth>().currentUser?.uid!!)
            .observe(this.viewLifecycleOwner, Observer {
                if (it is ViewModelState.Loading) {
                    binding.progressBarHome.visible(true)
                } else if (it is ViewModelState.Loaded<*>) {
                    binding.progressBarHome.visible(false)
                    try { //Fixes crash when user is being logged out
                        val listData = it.data as QuerySnapshot
                        homeScreenAdapter.add(listData.toObjects(MovieDb::class.java))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    // list.scheduleLayoutAnimation()
                }
            })

        homeViewModel.addGenres(viewLifecycleOwner)
        setErrorObserver()
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

    companion object {
        private const val TAG = "HomeFragment"
    }
}
