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
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afterroot.core.extensions.visible
import com.afterroot.tmdbapi.model.MovieDb
import com.afterroot.tmdbapi.model.Multi
import com.afterroot.tmdbapi.model.tv.TvSeries
import com.afterroot.watchdone.GlideApp
import com.afterroot.watchdone.R
import com.afterroot.watchdone.adapter.delegate.ItemSelectedCallback
import com.afterroot.watchdone.data.MultiDataHolder
import com.afterroot.watchdone.databinding.ListItemMovieBinding
import com.afterroot.watchdone.databinding.ListItemTvBinding
import com.afterroot.watchdone.ui.settings.Settings
import com.afterroot.watchdone.utils.getScreenWidth
import org.koin.core.KoinComponent
import org.koin.core.inject

class MultiAdapter(val callback: ItemSelectedCallback<MultiDataHolder>) :
    ListAdapter<MultiDataHolder, RecyclerView.ViewHolder>(MultiDiffCallback()),
    KoinComponent {
    val settings: Settings by inject()
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
        return when (getItem(position).data.mediaType) {
            Multi.MediaType.MOVIE -> {
                MOVIE
            }
            Multi.MediaType.TV_SERIES -> {
                TV
            }
            else -> -1
        }
    }

    private fun createTVViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return TVListViewHolder(ListItemTvBinding.inflate(LayoutInflater.from(parent.context)))
    }

    private fun createMovieViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return MoviesListViewHolder(ListItemMovieBinding.inflate(LayoutInflater.from(parent.context)))
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

    inner class MoviesListViewHolder(val binding: ListItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        private val posterView: AppCompatImageView = binding.poster
        private val context: Context = posterView.context
        private var heightRatio: Float = 3f / 2f
        private val width = context.getScreenWidth() / context.resources.getInteger(R.integer.grid_item_span_count)
        fun bind(movieDataHolder: MultiDataHolder) {
            binding.apply {
                movieDb = movieDataHolder.data as MovieDb
                root.setOnClickListener {
                    callback.onClick(absoluteAdapterPosition, root)
                    callback.onClick(absoluteAdapterPosition, root, movieDataHolder)
                }
                root.setOnLongClickListener {
                    callback.onLongClick(absoluteAdapterPosition, movieDataHolder)
                    return@setOnLongClickListener true
                }
                isWatched.visible(movieDataHolder.additionalParams?.isWatched ?: false)
            }
            posterView.updateLayoutParams {
                this.width = this@MoviesListViewHolder.width
                this.height = (width * heightRatio).toInt()
            }

            GlideApp.with(context).load(settings.baseUrl + settings.imageSize + binding.movieDb?.posterPath)
                .override(width, (width * heightRatio).toInt())
                .placeholder(R.drawable.ic_placeholder_movie)
                .error(R.drawable.ic_placeholder_movie)
                .centerCrop()
                .into(posterView)
        }
    }

    inner class TVListViewHolder(val binding: ListItemTvBinding) : RecyclerView.ViewHolder(binding.root) {
        private val posterView: AppCompatImageView = binding.poster
        private var heightRatio: Float = 3f / 2f
        private val context: Context = posterView.context
        private val width = context.getScreenWidth() / context.resources.getInteger(R.integer.grid_item_span_count)
        fun bind(tvDataHolder: MultiDataHolder) {
            binding.apply {
                tvSeries = tvDataHolder.data as TvSeries
                root.setOnClickListener {
                    callback.onClick(absoluteAdapterPosition, root)
                    callback.onClick(absoluteAdapterPosition, root, tvDataHolder)
                }
                root.setOnLongClickListener {
                    callback.onLongClick(absoluteAdapterPosition, tvDataHolder)
                    return@setOnLongClickListener true
                }
                isWatched.visible(tvDataHolder.additionalParams?.isWatched ?: false)
            }
            posterView.updateLayoutParams {
                this.width = this@TVListViewHolder.width
                this.height = (width * heightRatio).toInt()
            }

            GlideApp.with(context).load(settings.baseUrl + settings.imageSize + binding.tvSeries?.posterPath)
                .override(width, (width * heightRatio).toInt())
                .placeholder(R.drawable.ic_placeholder_tv)
                .error(R.drawable.ic_placeholder_tv)
                .centerCrop()
                .into(posterView)
        }
    }

    companion object {
        const val MOVIE = 0
        const val TV = 1
    }
}

class MultiDiffCallback : DiffUtil.ItemCallback<MultiDataHolder>() {
    override fun areItemsTheSame(oldItem: MultiDataHolder, newItem: MultiDataHolder): Boolean {
        val oldData = oldItem.data
        val newData = newItem.data
        return if (oldData is MovieDb && newData is MovieDb) {
            oldData.id == newData.id
        } else if (oldData is TvSeries && newData is TvSeries) {
            oldData.id == newData.id
        } else if (oldData is MovieDb && newData is TvSeries) {
            oldData.id == newData.id
        } else if (oldData is TvSeries && newData is MovieDb) {
            oldData.id == newData.id
        } else false
    }

    override fun areContentsTheSame(oldItem: MultiDataHolder, newItem: MultiDataHolder): Boolean {
        val oldData = oldItem.data
        val newData = newItem.data
        return if (oldData is MovieDb && newData is MovieDb) {
            oldData.equals(newData)
        } else if (oldData is TvSeries && newData is TvSeries) {
            oldData.equals(newData)
        } else if (oldData is MovieDb && newData is TvSeries) {
            false
        } else if (oldData is TvSeries && newData is MovieDb) {
            false
        } else false
    }
}