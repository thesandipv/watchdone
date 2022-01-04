package com.afterroot.watchdone.media.adapter.delegate

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afterroot.tmdbapi.Types
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.common.ItemSelectedCallback

class DelegateListAdapter(
    var settings: Settings,
    callback: DiffUtil.ItemCallback<Movie>,
    selectedCallback: ItemSelectedCallback<Movie>
) : ListAdapter<Movie, RecyclerView.ViewHolder>(callback) {
    private var delegateAdapters = SparseArrayCompat<AdapterType>()

    init {
        with(delegateAdapters) {
            put(Types.MOVIE, MovieAdapterType(selectedCallback, settings))
        }
        // stateRestorationPolicy = StateRestorationPolicy.PREVENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegateAdapters.get(viewType)!!.onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        @Suppress("UNCHECKED_CAST")
        delegateAdapters.get(getItemViewType(position))!!
            .onBindViewHolder(holder, getItem(position))
    }

    override fun getItemViewType(position: Int): Int = Types.MOVIE
}
