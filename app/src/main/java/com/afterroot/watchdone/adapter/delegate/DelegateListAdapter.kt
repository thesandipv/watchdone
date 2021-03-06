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

package com.afterroot.watchdone.adapter.delegate

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afterroot.tmdbapi.Types
import com.afterroot.tmdbapi.model.MovieDb
import com.afterroot.tmdbapi.model.core.AbstractJsonMapping
import com.afterroot.watchdone.data.base.AdditionalParams
import com.afterroot.watchdone.data.base.DataHolder
import com.afterroot.watchdone.data.movie.MovieDataHolder

class DelegateListAdapter(
    callback: DiffUtil.ItemCallback<MovieDataHolder>,
    selectedCallback: ItemSelectedCallback<MovieDb>
) : ListAdapter<MovieDataHolder, RecyclerView.ViewHolder>(callback) {
    private var delegateAdapters = SparseArrayCompat<AdapterType>()

    init {
        with(delegateAdapters) {
            put(Types.MOVIE, MovieAdapterType(selectedCallback))
        }
        //stateRestorationPolicy = StateRestorationPolicy.PREVENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegateAdapters.get(viewType)!!.onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        @Suppress("UNCHECKED_CAST")
        delegateAdapters.get(getItemViewType(position))!!
            .onBindViewHolder(holder, getItem(position) as DataHolder<AbstractJsonMapping, AdditionalParams>)
    }

    override fun getItemViewType(position: Int): Int = Types.MOVIE
}