/*
 * Copyright (C) 2020-2024 Sandip Vaghela
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

package com.afterroot.watchdone.data.daos

import app.tivi.data.daos.EntityDao
import com.afterroot.watchdone.data.model.Media
import kotlinx.coroutines.flow.Flow

interface MediaDao : EntityDao<Media> {
  fun getMediaByIds(ids: List<Long>): Flow<List<Media>>
  suspend fun getMediaByTmdbId(id: Int): Media?
  fun getMediaByIdFlow(id: Long): Flow<Media>
  suspend fun getMediaWithId(id: Long): Media?
  suspend fun getTmdbIdForMediaId(id: Long): Int?
  suspend fun getIdForTmdbId(tmdbId: Int): Long?
  suspend fun delete(id: Long)

  suspend fun deleteAll()
}

suspend fun MediaDao.getIdOrSaveMedia(media: Media): Long {
  val idForTmdbId: Long? = media.tmdbId?.let { getIdForTmdbId(it) }
  return when {
    idForTmdbId != null -> idForTmdbId
    else -> upsert(media)
  }
}

suspend fun MediaDao.getMediaByIdOrThrow(id: Long): Media =
  getMediaWithId(id) ?: throw IllegalArgumentException("No media with id $id in database")
