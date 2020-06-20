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

package com.afterroot.watchdone.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class BaseListAdapter<T>(diffCallback: DiffUtil.ItemCallback<T>) :
    ListAdapter<T, RecyclerView.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            HEADER -> {
                viewHolder = createHeaderViewHolder(parent)
            }
            ITEM -> {
                viewHolder = createItemViewHolder(parent)
            }
            FOOTER -> {
                viewHolder = createFooterViewHolder(parent)
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            HEADER -> {
                bindHeaderViewHolder(holder)
            }
            ITEM -> {
                bindItemViewHolder(holder, position)
            }
            FOOTER -> {
                bindFooterViewHolder(holder)
            }
        }
    }

    abstract fun createHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder?
    abstract fun createItemViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    abstract fun createFooterViewHolder(parent: ViewGroup): RecyclerView.ViewHolder?

    abstract fun bindHeaderViewHolder(viewHolder: RecyclerView.ViewHolder)
    abstract fun bindItemViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int)
    abstract fun bindFooterViewHolder(viewHolder: RecyclerView.ViewHolder)

    abstract fun addHeader()
    abstract fun addFooter()

    companion object {
        const val HEADER = 0
        const val ITEM = 1
        const val FOOTER = 2
    }
}

