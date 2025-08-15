/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.media

import app.tivi.data.db.DatabaseTransactionRunner
import app.tivi.data.util.storeBuilder
import com.afterroot.watchdone.base.CoroutineDispatchers
import com.afterroot.watchdone.data.daos.MediaDao
import com.afterroot.watchdone.data.daos.getMediaByIdOrThrow
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.data.util.mergeMedia
import javax.inject.Inject
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store

class MediaStore @Inject constructor(
  mediaDao: MediaDao,
  tmdbMovieDataSource: TmdbMovieDataSource,
  tmdbShowDataSource: TmdbShowDataSource,
  transactionRunner: DatabaseTransactionRunner,
  dispatchers: CoroutineDispatchers,
) : Store<MediaStoreRequest, Media> by storeBuilder(
  fetcher = Fetcher.of { request: MediaStoreRequest ->
    val savedMedia = withContext(dispatchers.databaseWrite) {
      mediaDao.getMediaByIdOrThrow(request.id)
    }

    val tmdbResult = runCatching {
      when (request.type) {
        MediaType.MOVIE -> tmdbMovieDataSource.getMovie(savedMedia)
        MediaType.SHOW -> tmdbShowDataSource.getShow(savedMedia)
        else -> throw IllegalArgumentException("MediaType should be MOVIE or SHOW")
      }
    }
    if (tmdbResult.isSuccess) {
      return@of tmdbResult.getOrThrow()
    }

    throw tmdbResult.exceptionOrNull()!!
  },
  sourceOfTruth = SourceOfTruth.of(
    reader = { request ->
      mediaDao.getMediaByIdFlow(request.id)
    },
    writer = { request, response ->
      transactionRunner {
        mediaDao.upsert(
          mergeMedia(local = mediaDao.getMediaByIdOrThrow(request.id), tmdb = response),
        )
      }
    },
    delete = { mediaDao.delete(it.id) },
    deleteAll = { transactionRunner(mediaDao::deleteAll) },
  ),
).build()

data class MediaStoreRequest(val id: Long, val type: MediaType)
