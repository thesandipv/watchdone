/*
 * Copyright (C) 2020-2023 Sandip Vaghela
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

package com.afterroot.watchdone.data.mapper

import app.moviebase.tmdb.model.TmdbMediaType
import app.moviebase.tmdb.model.TmdbShow
import app.moviebase.tmdb.model.TmdbShowDetail
import app.tivi.data.mappers.Mapper
import com.afterroot.watchdone.data.model.Media
import javax.inject.Inject

class TmdbShowToMedia @Inject constructor() : Mapper<TmdbShow, Media> {
    override fun map(from: TmdbShow) = Media(
        tmdbId = from.id,
        releaseDate = from.firstAirDate.toString(),
        title = from.name,
        isWatched = false,
        posterPath = from.posterPath,
        mediaType = TmdbMediaType.SHOW,
        rating = from.voteAverage.toDouble(),
        watched = null,
    )
}

class TmdbShowDetailToMedia @Inject constructor() : Mapper<TmdbShowDetail, Media> {
    override fun map(from: TmdbShowDetail) = Media(
        tmdbId = from.id,
        releaseDate = from.firstAirDate.toString(),
        title = from.name,
        isWatched = false,
        posterPath = from.posterPath,
        mediaType = TmdbMediaType.SHOW,
        rating = from.voteAverage.toDouble(),
        watched = null,
    )
}
