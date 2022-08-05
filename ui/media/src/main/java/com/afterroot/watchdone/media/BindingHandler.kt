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
package com.afterroot.watchdone.media

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.afterroot.tmdbapi.model.Genre
import com.afterroot.watchdone.base.GlideApp
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

private val crossFadeFactory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
internal val transitionOptions = DrawableTransitionOptions.with(crossFadeFactory)

@BindingAdapter("poster")
fun ImageView.poster(url: String?) {
    GlideApp.with(context).load(url).transition(transitionOptions)
        .into(this)
}

@BindingAdapter("genres")
fun ChipGroup.setGenres(genres: List<Genre>?) {
    removeAllViews()
    genres?.forEach { genre ->
        val chip = Chip(context)
        chip.text = genre.name
        addView(chip)
    }
}
