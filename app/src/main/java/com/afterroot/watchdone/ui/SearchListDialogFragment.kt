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

package com.afterroot.watchdone.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.afterroot.core.extensions.visible
import com.afterroot.tmdbapi.TmdbApi
import com.afterroot.tmdbapi.model.MovieDb
import com.afterroot.tmdbapi.tools.MovieDbException
import com.afterroot.watchdone.R
import com.afterroot.watchdone.adapter.DelegateAdapter
import com.afterroot.watchdone.adapter.ItemSelectedCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_search_list_dialog.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.toast
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin

class SearchListDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_list_dialog, container, false)
    }

    var searchTask: Job? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        input_search.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                searchTask = showSearchResults(input_search.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        searchTask?.apply {
            if (!isCompleted) cancel()
        }
    }

    private fun showSearchResults(title: String) = lifecycleScope.launch(Dispatchers.Main) {
        progress_bar.visible(true)
        try {
            val movies = withContext(Dispatchers.Default) { get<TmdbApi>().search.searchMovie(title) }
            progress_bar.visible(false)
            list.visible(true)
            val searchResultsAdapter = DelegateAdapter(object : ItemSelectedCallback<MovieDb> {
                override fun onClick(position: Int, view: View?, item: MovieDb) {
                    super.onClick(position, view, item)
                    view?.snackbar("Adding")
                    get<FirebaseFirestore>().collection("users")
                        .document(get<FirebaseAuth>().currentUser?.uid.toString()).collection("watchdone").document()
                        .set(item).addOnCompleteListener {
                            view?.snackbar("Added")
                        }
                    dismiss()
                }

                override fun onLongClick(position: Int, item: MovieDb) {
                    super.onLongClick(position, item)
                    requireContext().toast(item.title.toString())
                }
            }, getKoin())
            list.apply {
                val lm = GridLayoutManager(requireContext(), 2)
                layoutManager = lm
            }
            searchResultsAdapter.add(movies.results)
            list.adapter = searchResultsAdapter
            list.scheduleLayoutAnimation()
        } catch (mde: MovieDbException) {
            mde.printStackTrace()
            progress_bar.visible(false)
        }
    }
}