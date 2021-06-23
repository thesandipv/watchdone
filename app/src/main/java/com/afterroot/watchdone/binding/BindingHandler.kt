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
package com.afterroot.watchdone.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.afterroot.tmdbapi.model.NetworkMovie
import com.afterroot.tmdbapi.model.tv.TvSeries
import com.afterroot.tmdbapi2.model.Genre
import com.afterroot.watchdone.base.GlideApp
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.utils.getGravatarUrl
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

private val crossFadeFactory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
internal val transitionOptions = DrawableTransitionOptions.with(crossFadeFactory)

@BindingAdapter("avatar")
fun ImageView.setAvatar(email: String?) {
    GlideApp.with(context).load(getGravatarUrl(email.toString())).circleCrop().transition(transitionOptions).into(this)
}

@BindingAdapter("movieDb", "settings")
fun ImageView.setMoviePoster(movieDb: NetworkMovie?, settings: Settings?) {
    GlideApp.with(context).load(settings?.baseUrl + settings?.imageSize + movieDb?.posterPath).transition(transitionOptions)
        .into(this)
}

@BindingAdapter("tvSeries", "settings")
fun ImageView.setTVPoster(tvSeries: TvSeries?, settings: Settings?) {
    GlideApp.with(context).load(settings?.baseUrl + settings?.imageSize + tvSeries?.posterPath).transition(transitionOptions)
        .into(this)
}

@BindingAdapter("poster")
fun ImageView.poster(url: String) {
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
