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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afterroot.core.extensions.visible
import com.afterroot.tmdbapi.TmdbApi
import com.afterroot.tmdbapi.model.MovieDb
import com.afterroot.watchdone.R
import com.afterroot.watchdone.adapter.DelegateAdapter
import com.afterroot.watchdone.adapter.ItemSelectedCallback
import kotlinx.android.synthetic.main.content_add_watched.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.list_dialog.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.toast
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin

class HomeFragment : Fragment() {
    lateinit var dialogCustomView: View
    private lateinit var homeViewModel: HomeViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onResume() {
        super.onResume()
        button_action_add_watched.setOnClickListener {
            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(text = "Add Watched")
                customView(R.layout.content_add_watched)
                dialogCustomView = getCustomView()
                positiveButton(text = "Add") {
                    lifecycleScope.launch {
                        //TODO Add Task
                        showSelectDialog(input_title.text.toString())
                    }
                }
            }
        }
    }

    private fun showSelectDialog(title: String) = GlobalScope.launch(Dispatchers.Main) {
        progress_bar.visible(true)
        val movies = withContext(Dispatchers.Default) { get<TmdbApi>().search.searchMovie(title) }
        progress_bar.visible(false)
        Log.d(TAG, "showSelectDialog: ${movies.results}")
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
            title(text = "Add Watched")
            customView(R.layout.list_dialog)
            dialogCustomView = getCustomView().apply {
                val adapter = DelegateAdapter(object : ItemSelectedCallback<MovieDb> {
                    override fun onClick(position: Int, view: View?, item: MovieDb) {
                        super.onClick(position, view, item)
                        requireContext().toast(item.title.toString())
                    }
                }, getKoin())
                list.apply {
                    val lm = GridLayoutManager(requireContext(), 2)
                    layoutManager = lm
                }
                adapter.add(movies.results)
                list.adapter = adapter
                list.scheduleLayoutAnimation()

            }
        }
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}
