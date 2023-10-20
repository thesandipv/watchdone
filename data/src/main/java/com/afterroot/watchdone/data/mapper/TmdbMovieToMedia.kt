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
import app.moviebase.tmdb.model.TmdbMovie
import app.moviebase.tmdb.model.TmdbMovieDetail
import app.tivi.data.mappers.Mapper
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.database.model.MediaEntity
import javax.inject.Inject

class TmdbMovieToMedia @Inject constructor() : Mapper<TmdbMovie, Media> {
    override fun map(from: TmdbMovie) = Media(
        tmdbId = from.id,
        releaseDate = from.releaseDate.toString(),
        title = from.title,
        isWatched = false,
        posterPath = from.posterPath,
        mediaType = TmdbMediaType.MOVIE,
        rating = from.voteAverage.toDouble(),
        watched = null,
    )
}

class TmdbMovieDetailToMedia @Inject constructor() : Mapper<TmdbMovieDetail, Media> {
    override fun map(from: TmdbMovieDetail) = Media(
        tmdbId = from.id,
        releaseDate = from.releaseDate.toString(),
        title = from.title,
        isWatched = false,
        posterPath = from.posterPath,
        mediaType = TmdbMediaType.MOVIE,
        rating = from.voteAverage.toDouble(),
        watched = null,
    )
}

class MediaToMediaEntity @Inject constructor() : Mapper<Media, MediaEntity> {
    override fun map(from: Media) = MediaEntity(
        tmdbId = from.tmdbId,
        releaseDate = from.releaseDate.toString(),
        title = from.title,
        isWatched = from.isWatched,
        posterPath = from.posterPath,
        mediaType = from.mediaType,
        rating = from.rating,
    )
}

class MediaEntityToMedia @Inject constructor() : Mapper<MediaEntity, Media> {
    override fun map(from: MediaEntity) = Media(
        tmdbId = from.tmdbId,
        releaseDate = from.releaseDate,
        title = from.title,
        isWatched = from.isWatched,
        posterPath = from.posterPath,
        mediaType = from.mediaType,
        rating = from.rating,
        watched = listOf(),
    )
}
