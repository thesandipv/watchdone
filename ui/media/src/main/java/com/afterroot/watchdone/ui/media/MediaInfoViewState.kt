/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.ui.media

import androidx.compose.runtime.Immutable
import app.moviebase.tmdb.model.TmdbCredits
import app.tivi.api.UiMessage
import com.afterroot.watchdone.base.compose.ViewState
import com.afterroot.watchdone.data.model.DBMedia
import com.afterroot.watchdone.data.model.Genre
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.Season
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.data.model.WatchProviderResult
import com.afterroot.watchdone.utils.State

@Immutable
data class MediaInfoViewState(
  val credits: State<TmdbCredits> = State.loading(),
  val empty: Boolean = true,
  val isLoading: Boolean = false,
  val mediaId: Int = 0,
  val media: DBMedia = DBMedia.Empty,
  val mediaType: MediaType? = MediaType.MOVIE,
  val movie: Movie = Movie.Empty,
  val refresh: Boolean = false,
  val seasonInfo: State<Season> = State.loading(),
  val selectedSeason: Int = 1,
  val tv: TV = TV.Empty,
  val isInWatchlist: State<Boolean> = State.loading(),
  val isWatched: State<Boolean> = State.loading(),
  val genres: List<Genre> = emptyList(),
  val watchProviders: State<WatchProviderResult> = State.loading(),
  override val message: UiMessage? = null,
) : ViewState() {
  companion object {
    val Empty = MediaInfoViewState()
  }
}
