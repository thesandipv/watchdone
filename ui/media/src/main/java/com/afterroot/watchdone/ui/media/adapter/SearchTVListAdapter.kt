/*
 * Copyright (C) 2020-2022 Sandip Vaghela
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
package com.afterroot.watchdone.ui.media.adapter

import android.content.Context
import android.content.ContextWrapper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.afterroot.watchdone.base.GlideApp
import com.afterroot.watchdone.base.adapter.BaseListAdapter
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.diff.TVDiffCallback
import com.afterroot.watchdone.media.databinding.ListItemTvBinding
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.common.ItemSelectedCallback
import com.afterroot.watchdone.utils.getScreenWidth
import com.afterroot.watchdone.resources.R as CommonR

class SearchTVListAdapter(val callback: ItemSelectedCallback<TV>, var settings: Settings) :
    BaseListAdapter<TV>(TVDiffCallback()) {
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
        private val context: Context = (binding.root.context as ContextWrapper).baseContext
        private val width =
            (context.getScreenWidth() / context.resources.getInteger(CommonR.integer.horizontal_grid_max_visible)) - context.resources.getDimensionPixelSize(
                CommonR.dimen.padding_horizontal_list
            )

        fun bind(tvDataHolder: TV) {
            binding.apply {
                tvSeries = tvDataHolder
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

            GlideApp.with(context).load(settings.baseUrl + settings.imageSize + tvDataHolder.posterPath)
                .override(width, (width * heightRatio).toInt())
                .placeholder(CommonR.drawable.ic_placeholder_tv)
                .error(CommonR.drawable.ic_placeholder_tv)
                .centerCrop()
                .into(posterView)
        }
    }
}
