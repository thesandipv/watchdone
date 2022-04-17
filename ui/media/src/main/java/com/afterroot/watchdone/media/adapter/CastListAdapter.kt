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
package com.afterroot.watchdone.media.adapter

import android.content.Context
import android.content.ContextWrapper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.afterroot.tmdbapi.model.people.Person
import com.afterroot.tmdbapi.model.people.PersonCast
import com.afterroot.tmdbapi.model.people.PersonCrew
import com.afterroot.watchdone.base.GlideApp
import com.afterroot.watchdone.base.adapter.BaseListAdapter
import com.afterroot.watchdone.diff.CastDiffCallback
import com.afterroot.watchdone.media.databinding.ListItemCastBinding
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.utils.getScreenWidth
import com.afterroot.watchdone.resources.R as CommonR

class CastListAdapter(var settings: Settings) : BaseListAdapter<Person>(CastDiffCallback()) {
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

    inner class CastListViewHolder(val binding: ListItemCastBinding) : RecyclerView.ViewHolder(binding.root) {
        private val posterView: AppCompatImageView = binding.castIv
        private var heightRatio: Float = 3f / 2f
        private val context: Context = (binding.root.context as ContextWrapper).baseContext
        private val width =
            (context.getScreenWidth() / context.resources.getInteger(CommonR.integer.horizontal_grid_max_visible)) - context.resources.getDimensionPixelSize(
                CommonR.dimen.padding_horizontal_list
            )

        fun bind(personCast: Person) {
            if (personCast is PersonCast) {
                binding.personDetail = personCast
            } else if (personCast is PersonCrew) {
                binding.personCrew = personCast
            }
            posterView.updateLayoutParams {
                this.width = this@CastListViewHolder.width
                this.height = (width * heightRatio).toInt()
            }

            var imageUrl: String? = null
            if (personCast.profilePath != null) {
                imageUrl = settings.baseUrl + settings.imageSize + personCast.profilePath
            }
            GlideApp.with(context).load(imageUrl)
                .override(width, (width * heightRatio).toInt())
                .placeholder(CommonR.drawable.ic_person)
                .error(CommonR.drawable.ic_person)
                .centerCrop()
                .into(posterView)
        }
    }
}
