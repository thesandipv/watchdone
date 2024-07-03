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
import app.tivi.data.mappers.Mapper
import com.afterroot.watchdone.data.model.MediaType
import javax.inject.Inject

class TmdbMediaTypeToMediaType @Inject constructor() : Mapper<TmdbMediaType, MediaType> {
  override fun map(from: TmdbMediaType): MediaType = when (from) {
    TmdbMediaType.MOVIE -> MediaType.MOVIE
    TmdbMediaType.SHOW -> MediaType.SHOW
    TmdbMediaType.SEASON -> MediaType.SEASON
    TmdbMediaType.EPISODE -> MediaType.EPISODE
  }
}

class MediaTypeToTmdbMediaType @Inject constructor() : Mapper<MediaType, TmdbMediaType> {
  override fun map(from: MediaType): TmdbMediaType = when (from) {
    MediaType.MOVIE -> TmdbMediaType.MOVIE
    MediaType.SHOW -> TmdbMediaType.SHOW
    MediaType.SEASON -> TmdbMediaType.SEASON
    MediaType.EPISODE -> TmdbMediaType.EPISODE
  }
}
