/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
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
