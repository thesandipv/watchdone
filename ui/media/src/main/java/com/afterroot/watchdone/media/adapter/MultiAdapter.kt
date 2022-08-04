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
package com.afterroot.watchdone.media.adapter

import android.content.Context
import android.content.ContextWrapper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afterroot.tmdbapi.model.Multi
import com.afterroot.utils.extensions.visible
import com.afterroot.watchdone.base.GlideApp
import com.afterroot.watchdone.binding.transitionOptions
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.diff.MultiDiffCallback
import com.afterroot.watchdone.media.databinding.ListItemMovieBinding
import com.afterroot.watchdone.media.databinding.ListItemTvBinding
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.common.ItemSelectedCallback
import com.afterroot.watchdone.utils.getScreenWidth
import com.afterroot.watchdone.resources.R as CommonR

class MultiAdapter(val callback: ItemSelectedCallback<Multi>, var settings: Settings) :
    ListAdapter<Multi, RecyclerView.ViewHolder>(MultiDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            MOVIE -> {
                viewHolder = createMovieViewHolder(parent)
            }
            TV -> {
                viewHolder = createTVViewHolder(parent)
            }
        }
        return viewHolder!!
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).mediaType) {
            Multi.MediaType.MOVIE -> {
                MOVIE
            }
            Multi.MediaType.TV_SERIES -> {
                TV
            }
            else -> -1
        }
    }

    private fun createTVViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return TVListViewHolder(
            ListItemTvBinding.inflate(LayoutInflater.from(parent.context)).apply {
                baseUrl = settings.baseUrl
                posterSize = settings.imageSize
            },
            callback
        )
    }

    private fun createMovieViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return MoviesListViewHolder(
            ListItemMovieBinding.inflate(LayoutInflater.from(parent.context)).apply {
                baseUrl = settings.baseUrl
                posterSize = settings.imageSize
            },
            callback
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            MOVIE -> {
                bindMovieViewHolder(holder, position)
            }
            TV -> {
                bindTVViewHolder(holder, position)
            }
        }
    }

    private fun bindTVViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as TVListViewHolder
        holder.bind(getItem(position))
    }

    private fun bindMovieViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as MoviesListViewHolder
        holder.bind(getItem(position))
    }

    companion object {
        const val MOVIE = 0
        const val TV = 1
    }
}

class MoviesListViewHolder(
    val binding: ListItemMovieBinding,
    val callback: ItemSelectedCallback<Multi>
) : RecyclerView.ViewHolder(binding.root) {
    private val posterView: AppCompatImageView = binding.poster
    private val context: Context = (binding.root.context as ContextWrapper).baseContext
    private var heightRatio: Float = 3f / 2f
    private val width = context.getScreenWidth() / context.resources.getInteger(CommonR.integer.grid_item_span_count)
    fun bind(movie: Multi) {
        binding.apply {
            movieDb = movie as Movie
            root.setOnClickListener {
                callback.onClick(bindingAdapterPosition, root)
                callback.onClick(bindingAdapterPosition, root, movie)
            }
            root.setOnLongClickListener {
                callback.onLongClick(bindingAdapterPosition, movie)
                return@setOnLongClickListener true
            }
            isWatched.visible(movie.isWatched)
        }
        posterView.updateLayoutParams {
            this.width = this@MoviesListViewHolder.width
            this.height = (width * heightRatio).toInt()
        }

        GlideApp.with(context).load(binding.baseUrl + binding.posterSize + binding.movieDb?.posterPath)
            .override(width, (width * heightRatio).toInt())
            .error(CommonR.drawable.ic_placeholder_movie)
            .transition(transitionOptions)
            .centerCrop()
            .into(posterView)
    }
}

class TVListViewHolder(
    val binding: ListItemTvBinding,
    val callback: ItemSelectedCallback<Multi>
) : RecyclerView.ViewHolder(binding.root) {
    private val posterView: AppCompatImageView = binding.poster
    private var heightRatio: Float = 3f / 2f
    private val context: Context = (binding.root.context as ContextWrapper).baseContext
    private val width = context.getScreenWidth() / context.resources.getInteger(CommonR.integer.grid_item_span_count)
    fun bind(tv: Multi) {
        binding.apply {
            tvSeries = tv as TV
            root.setOnClickListener {
                callback.onClick(bindingAdapterPosition, root)
                callback.onClick(bindingAdapterPosition, root, tv)
            }
            root.setOnLongClickListener {
                callback.onLongClick(bindingAdapterPosition, tv)
                return@setOnLongClickListener true
            }
            isWatched.visible(tv.isWatched)
        }
        posterView.updateLayoutParams {
            this.width = this@TVListViewHolder.width
            this.height = (width * heightRatio).toInt()
        }

        GlideApp.with(context).load(binding.baseUrl + binding.posterSize + binding.tvSeries?.posterPath)
            .override(width, (width * heightRatio).toInt())
            .error(CommonR.drawable.ic_placeholder_tv)
            .transition(transitionOptions)
            .centerCrop()
            .into(posterView)
    }
}
