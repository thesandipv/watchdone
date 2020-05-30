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
import androidx.recyclerview.widget.GridLayoutManager
import com.afterroot.core.extensions.visible
import com.afterroot.tmdbapi.model.MovieDb
import com.afterroot.watchdone.R
import com.afterroot.watchdone.adapter.DelegateAdapter
import com.afterroot.watchdone.adapter.ItemSelectedCallback
import com.afterroot.watchdone.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
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
        binding.list.apply {
            val lm = GridLayoutManager(requireContext(), 2)
            layoutManager = lm
            adapter = homeScreenAdapter
        }
        homeViewModel.getWatchlistSnapshot(get<FirebaseAuth>().currentUser?.uid!!)
            .observe(this.viewLifecycleOwner, Observer {
                if (it is ViewModelState.Loading) {
                    binding.progressBar.visible(true)
                } else if (it is ViewModelState.Loaded<*>) {
                    binding.progressBar.visible(false)
                    val listData = it.data as QuerySnapshot
                    homeScreenAdapter.add(listData.toObjects(MovieDb::class.java))
                    // list.scheduleLayoutAnimation()
                }
            })

        homeViewModel.addGenres(viewLifecycleOwner)
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}
