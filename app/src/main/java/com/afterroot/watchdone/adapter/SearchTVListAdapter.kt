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
package com.afterroot.watchdone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.afterroot.watchdone.GlideApp
import com.afterroot.watchdone.R
import com.afterroot.watchdone.adapter.base.BaseListAdapter
import com.afterroot.watchdone.adapter.delegate.ItemSelectedCallback
import com.afterroot.watchdone.adapter.diff.TVDiffCallback
import com.afterroot.watchdone.data.tv.TVDataHolder
import com.afterroot.watchdone.databinding.ListItemTvBinding
import com.afterroot.watchdone.ui.settings.Settings
import com.afterroot.watchdone.utils.getScreenWidth
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SearchTVListAdapter(val callback: ItemSelectedCallback<TVDataHolder>) :
    BaseListAdapter<TVDataHolder>(TVDiffCallback()), KoinComponent {
    val settings: Settings by inject()
    override fun createHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return null
    }

    override fun createItemViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return TVListViewHolder(ListItemTvBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun createFooterViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return null
    }

    override fun bindHeaderViewHolder(viewHolder: RecyclerView.ViewHolder) {
    }

    override fun bindItemViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        viewHolder as TVListViewHolder
        viewHolder.bind(getItem(position))
    }

    override fun bindFooterViewHolder(viewHolder: RecyclerView.ViewHolder) {
    }

    override fun addHeader() {
    }

    override fun addFooter() {
    }

    override fun getItemViewType(position: Int): Int {
        return ITEM
    }

    inner class TVListViewHolder(val binding: ListItemTvBinding) : RecyclerView.ViewHolder(binding.root) {
        private val posterView: AppCompatImageView = binding.poster
        private var heightRatio: Float = 3f / 2f
        private val context: Context = posterView.context
        private val width =
            (context.getScreenWidth() / context.resources.getInteger(R.integer.horizontal_grid_max_visible)) - context.resources.getDimensionPixelSize(
                R.dimen.padding_horizontal_list
            )

        fun bind(tvDataHolder: TVDataHolder) {
            binding.apply {
                tvSeries = tvDataHolder.data
                root.setOnClickListener {
                    callback.onClick(adapterPosition, root)
                    callback.onClick(adapterPosition, root, tvDataHolder)
                }
                root.setOnLongClickListener {
                    callback.onLongClick(adapterPosition, tvDataHolder)
                    return@setOnLongClickListener true
                }
            }
            posterView.updateLayoutParams {
                this.width = this@TVListViewHolder.width
                this.height = (width * heightRatio).toInt()
            }

            GlideApp.with(context).load(settings.baseUrl + settings.imageSize + tvDataHolder.data.posterPath)
                .override(width, (width * heightRatio).toInt())
                .placeholder(R.drawable.ic_placeholder_tv)
                .error(R.drawable.ic_placeholder_tv)
                .centerCrop()
                .into(posterView)
        }
    }
}
