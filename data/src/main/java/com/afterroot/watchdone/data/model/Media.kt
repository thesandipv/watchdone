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

package com.afterroot.watchdone.data.model

import com.afterroot.watchdone.data.network.model.NetworkMedia
import com.afterroot.watchdone.database.model.MediaEntity

fun NetworkMedia.asEntity() = MediaEntity(
    id = id,
    releaseDate = releaseDate,
    title = title,
    isWatched = isWatched,
    posterPath = posterPath,
    mediaType = mediaType,
    rating = rating,
)

fun MediaEntity.asExternalModel() = Media(
    id = id,
    releaseDate = releaseDate,
    title = title,
    isWatched = isWatched,
    posterPath = posterPath,
    mediaType = mediaType,
    rating = rating,
    watched = listOf(),
)
