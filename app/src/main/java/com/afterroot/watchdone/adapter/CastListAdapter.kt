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
import com.afterroot.tmdbapi.model.people.PersonCast
import com.afterroot.watchdone.GlideApp
import com.afterroot.watchdone.R
import com.afterroot.watchdone.adapter.base.BaseListAdapter
import com.afterroot.watchdone.adapter.diff.CastDiffCallback
import com.afterroot.watchdone.data.cast.CastDataHolder
import com.afterroot.watchdone.databinding.ListItemCastBinding
import com.afterroot.watchdone.ui.settings.Settings
import com.afterroot.watchdone.utils.getScreenWidth
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CastListAdapter : BaseListAdapter<CastDataHolder>(CastDiffCallback()), KoinComponent {
    val settings: Settings by inject()
    override fun createHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return null
    }

    override fun createItemViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return CastListViewHolder(ListItemCastBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun createFooterViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return null
    }

    override fun bindHeaderViewHolder(viewHolder: RecyclerView.ViewHolder) {
    }

    override fun bindItemViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        viewHolder as CastListViewHolder
        viewHolder.bind(getItem(position).data)
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

    inner class CastListViewHolder(val binding: ListItemCastBinding) : RecyclerView.ViewHolder(binding.root) {
        private val posterView: AppCompatImageView = binding.castIv
        val context: Context = posterView.context
        var heightRatio: Float = 3f / 2f
        val width =
            (context.getScreenWidth() / context.resources.getInteger(R.integer.horizontal_grid_max_visible)) - context.resources.getDimensionPixelSize(
                R.dimen.padding_horizontal_list
            )

        fun bind(personCast: PersonCast) {
            binding.personDetail = personCast
            posterView.updateLayoutParams {
                this.width = this@CastListViewHolder.width
                this.height = (width * heightRatio).toInt()
            }

            GlideApp.with(context).load(settings.baseUrl + settings.imageSize + personCast.profilePath)
                .override(width, (width * heightRatio).toInt())
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .centerCrop()
                .into(posterView)
        }
    }
}