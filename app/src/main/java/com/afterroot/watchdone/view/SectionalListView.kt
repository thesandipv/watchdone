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
package com.afterroot.watchdone.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import com.afterroot.core.extensions.visible
import com.afterroot.watchdone.R

class SectionalListView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayoutCompat(context, attrs, defStyleAttr) {
    private val progress: ProgressBar
    private val titleView: AppCompatTextView
    val list: RecyclerView
    var title: String? = null

    init {
        inflate(context, R.layout.layout_sectional_list, this).apply {
            titleView = this.findViewById(R.id.slv_title)
            list = this.findViewById(R.id.slv_list)
            progress = this.findViewById(R.id.slv_progress)
        }
    }

    fun withTitle(title: String): SectionalListView {
        this.title = title
        titleView.text = title
        return this
    }

    fun withLoading(): SectionalListView {
        this.isLoading = true
        return this
    }

    var isLoading: Boolean = false
        set(value) {
            field = value
            titleView.visible(true, AutoTransition())
            list.visible(!field, AutoTransition())
            progress.visible(field)
        }

    var isLoaded: Boolean = false
        set(value) {
            field = value
            titleView.visible(field, AutoTransition())
            list.visible(true, AutoTransition())
            progress.visible(false)
        }

    fun noResults() {
        hide()
    }

    fun hide() {
        this.visible(false, AutoTransition())
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        this.list.adapter = adapter
    }
}
