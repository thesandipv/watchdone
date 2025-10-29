/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.data.mapper

import app.moviebase.tmdb.model.TmdbEpisode
import app.moviebase.tmdb.model.TmdbEpisodeDetail
import com.afterroot.watchdone.data.model.Episode
import com.afterroot.watchdone.data.model.Episodes
import com.afterroot.watchdone.data.model.TmdbEpisodeDetails
import com.afterroot.watchdone.data.model.TmdbEpisodes

fun TmdbEpisode.toEpisode(): Episode = Episode(
  id = id,
  seasonNumber = seasonNumber,
  episodeNumber = episodeNumber,
  airDate = airDate,
  crew = crew,
  guestStars = guestStars,
  name = name,
  overview = overview,
  stillPath = stillPath,
  voteAverage = voteAverage,
  voteCount = voteCount,
)

fun TmdbEpisodeDetail.toEpisode(): Episode = Episode(
  id = id,
  seasonNumber = seasonNumber,
  episodeNumber = episodeNumber,
  airDate = airDate,
  crew = crew,
  guestStars = guestStars,
  name = name,
  overview = overview,
  stillPath = stillPath,
  voteAverage = voteAverage,
  voteCount = voteCount,
)

fun TmdbEpisodes.toEpisodes(): Episodes = this?.map {
  it.toEpisode()
}

fun TmdbEpisodeDetails.asEpisodes(): Episodes = this?.map {
  it.toEpisode()
}
