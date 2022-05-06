package com.afterroot.watchdone.media.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afterroot.tmdbapi.model.Multi
import com.afterroot.watchdone.diff.MultiDiffCallback
import com.afterroot.watchdone.media.databinding.ListItemMovieBinding
import com.afterroot.watchdone.media.databinding.ListItemTvBinding
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.common.ItemSelectedCallback

class MultiPagingAdapter(private val callback: ItemSelectedCallback<Multi>, var settings: Settings) :
    PagingDataAdapter<Multi, RecyclerView.ViewHolder>(MultiDiffCallback()) {

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
        return when (getItem(position)?.mediaType) {
            Multi.MediaType.MOVIE -> {
                MOVIE
            }
            Multi.MediaType.TV_SERIES -> {
                TV
            }
            else -> -1
        }
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
        getItem(position)?.let { holder.bind(it) }
    }

    private fun bindMovieViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as MoviesListViewHolder
        getItem(position)?.let { holder.bind(it) }
    }

    companion object {
        const val MOVIE = 0
        const val TV = 1
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
}
