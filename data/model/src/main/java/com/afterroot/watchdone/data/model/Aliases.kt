/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.data.model

import app.moviebase.tmdb.model.TmdbEpisode
import app.moviebase.tmdb.model.TmdbEpisodeDetail
import app.moviebase.tmdb.model.TmdbKeywordDetail
import app.moviebase.tmdb.model.TmdbSeasonDetail
import app.moviebase.tmdb.model.TmdbVideo

typealias TmdbEpisodes = List<TmdbEpisode>?
typealias TmdbEpisodeDetails = List<TmdbEpisodeDetail>?

typealias Episodes = List<Episode>?

typealias TmdbSeasons = List<TmdbSeasonDetail>?
typealias Seasons = List<Season>?

typealias Keywords = List<TmdbKeywordDetail>?

typealias Videos = List<TmdbVideo>?
