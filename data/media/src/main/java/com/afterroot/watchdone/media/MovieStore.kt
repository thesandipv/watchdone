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

// TODO Show implementation pending
class MovieStore @Inject constructor(
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
