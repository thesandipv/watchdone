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

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.afterroot.core.extensions.getDrawableExt
import com.afterroot.tmdbapi.model.MovieDb
import com.afterroot.tmdbapi.model.core.AbstractJsonMapping
import com.afterroot.watchdone.GlideApp
import com.afterroot.watchdone.R
import com.afterroot.watchdone.databinding.ListItemMovieBinding
import com.afterroot.watchdone.ui.settings.Settings
import com.afterroot.watchdone.utils.getScreenWidth
import kotlinx.android.synthetic.main.list_item_movie.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class MovieAdapterType(val callbacks: ItemSelectedCallback<MovieDb>) : AdapterType, KoinComponent {
    val settings: Settings by inject()
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        MovieVH(ListItemMovieBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: AbstractJsonMapping) {
        holder as MovieVH
        holder.bind(item as MovieDb)
    }

    inner class MovieVH(val binding: ListItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        private val posterView: AppCompatImageView = itemView.poster

        // val view = ListItemMovieBinding.inflate(LayoutInflater.from(parent.context))
        val context: Context = posterView.context
        var heightRatio: Float = 3f / 2f
        val width = context.getScreenWidth() / context.resources.getInteger(R.integer.grid_item_span_count)
        fun bind(item: MovieDb) {
            binding.movieDb = item
            if (item.posterPath != null) {
                GlideApp.with(context).load(settings.baseUrl + settings.imageSize + item.posterPath)
                    .override(width, (width * heightRatio).toInt())
                    .centerCrop()
                    .into(posterView)
            } else GlideApp.with(context).load(context.getDrawableExt(R.drawable.ic_broken_image))
                .override(posterView.context.resources.getDimensionPixelSize(R.dimen.placeholder_image_size))
                .into(posterView)
            with(super.itemView) {
                tag = item
                setOnClickListener {
                    callbacks.onClick(bindingAdapterPosition, itemView, item)
                }
                setOnLongClickListener {
                    callbacks.onLongClick(bindingAdapterPosition, item)
                    return@setOnLongClickListener true
                }
            }
        }
    }
}