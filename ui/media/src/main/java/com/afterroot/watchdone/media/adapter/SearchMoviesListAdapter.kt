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
import com.afterroot.watchdone.base.GlideApp
import com.afterroot.watchdone.base.adapter.BaseListAdapter
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.diff.MovieDiffCallback
import com.afterroot.watchdone.media.R
import com.afterroot.watchdone.media.databinding.ListItemMovieBinding
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.common.ItemSelectedCallback
import com.afterroot.watchdone.utils.getScreenWidth

class SearchMoviesListAdapter(val callback: ItemSelectedCallback<Movie>, var settings: Settings) :
    BaseListAdapter<Movie>(MovieDiffCallback()) {
    override fun createHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return null
    }

    override fun createItemViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return MoviesListViewHolder(ListItemMovieBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun createFooterViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return null
    }

    override fun bindHeaderViewHolder(viewHolder: RecyclerView.ViewHolder) {
    }

    override fun bindItemViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        viewHolder as MoviesListViewHolder
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

    inner class MoviesListViewHolder(val binding: ListItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        private val posterView: AppCompatImageView = binding.poster
        private val context: Context = (binding.root.context as ContextWrapper).baseContext
        private var heightRatio: Float = 3f / 2f
        private val width =
            (context.getScreenWidth() / context.resources.getInteger(R.integer.horizontal_grid_max_visible)) - context.resources.getDimensionPixelSize(
                R.dimen.padding_horizontal_list
            )

        fun bind(movie: Movie) {
            binding.apply {
                movieDb = movie
                root.setOnClickListener {
                    callback.onClick(adapterPosition, root)
                    callback.onClick(adapterPosition, root, movie)
                }
                root.setOnLongClickListener {
                    callback.onLongClick(adapterPosition, movie)
                    return@setOnLongClickListener true
                }
            }
            posterView.updateLayoutParams {
                this.width = this@MoviesListViewHolder.width
                this.height = (width * heightRatio).toInt()
            }

            GlideApp.with(context).load(settings.baseUrl + settings.imageSize + movie.posterPath)
                .override(width, (width * heightRatio).toInt())
                .placeholder(R.drawable.ic_placeholder_movie)
                .error(R.drawable.ic_placeholder_movie)
                .centerCrop()
                .into(posterView)
        }
    }
}
