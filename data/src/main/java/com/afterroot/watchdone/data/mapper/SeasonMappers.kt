/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.data.mapper

import app.moviebase.tmdb.model.TmdbSeasonDetail
import com.afterroot.watchdone.data.model.Season
import com.afterroot.watchdone.data.model.Seasons
import com.afterroot.watchdone.data.model.TmdbSeasons

fun TmdbSeasonDetail.toSeason(): Season = Season(
  airDate = airDate,
  episodeCount = episodeCount,
  episodes = episodes,
  id = id,
  name = name,
  overview = overview,
  posterPath = posterPath,
  seasonNumber = seasonNumber,
  voteAverage = voteAverage,

  // Appendable responses
  videos = videos,
  images = images,
  externalIds = externalIds,

  // Additional Data
  isWatched = false,
)

fun TmdbSeasons.toSeasons(): Seasons = this?.map {
  it.toSeason()
}
