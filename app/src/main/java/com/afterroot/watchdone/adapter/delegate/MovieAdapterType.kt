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

import android.content.Context
import android.content.ContextWrapper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.afterroot.core.extensions.visible
import com.afterroot.tmdbapi.model.Multi
import com.afterroot.watchdone.R
import com.afterroot.watchdone.base.GlideApp
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.media.databinding.ListItemMovieBinding
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.common.ItemSelectedCallback
import com.afterroot.watchdone.utils.getScreenWidth

class MovieAdapterType(val callbacks: ItemSelectedCallback<Movie>, var settings: Settings) : AdapterType {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        MovieVH(ListItemMovieBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Multi) {
        holder as MovieVH
        holder.bind(item as Movie)
    }

    inner class MovieVH(val binding: ListItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        private val posterView: AppCompatImageView = binding.poster
        private var heightRatio: Float = 3f / 2f
        val context: Context = (binding.root.context as ContextWrapper).baseContext
        private val width = context.getScreenWidth() / context.resources.getInteger(R.integer.grid_item_span_count)
        fun bind(item: Movie) {
            binding.movieDb = item
            posterView.updateLayoutParams {
                this.width = this@MovieVH.width
                this.height = (width * heightRatio).toInt()
            }
            binding.isWatched.visible(item.isWatched)
            // if (item.posterPath != null) {
            GlideApp.with(context).load(settings.baseUrl + settings.imageSize + item.posterPath)
                .override(width, (width * heightRatio).toInt())
                .placeholder(R.drawable.ic_broken_image)
                .error(R.drawable.ic_broken_image)
                .centerCrop()
                .into(posterView)
            // } else GlideApp.with(context).load(R.drawable.ic_broken_image)
            //     .into(posterView)
            with(super.itemView) {
                tag = item
                setOnClickListener {
                    callbacks.onClick(adapterPosition, itemView, item)
                }
                setOnLongClickListener {
                    callbacks.onLongClick(adapterPosition, item)
                    return@setOnLongClickListener true
                }
            }
        }
    }
}
