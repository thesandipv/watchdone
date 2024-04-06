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
import app.tivi.util.Logger
import com.afterroot.watchdone.data.daos.MediaDao
import com.afterroot.watchdone.data.daos.getMediaByIdOrThrow
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.util.mergeMedia
import javax.inject.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store

// TODO Show implementation pending
class MovieStore @Inject constructor(
  mediaDao: MediaDao,
  tmdbMovieDataSource: TmdbMovieDataSource,
  transactionRunner: DatabaseTransactionRunner,
  logger: Logger,
) : Store<Long, Media> by storeBuilder(
  fetcher = Fetcher.of { id: Long ->
    val savedMedia = mediaDao.getMediaByIdOrThrow(id)

    return@of run { tmdbMovieDataSource.getMovie(savedMedia) }
  },
  sourceOfTruth = SourceOfTruth.of(
    reader = { movieId ->
      mediaDao.getMediaByIdFlow(movieId)
    },
    writer = { id, response ->
      logger.d {
        "Writing in database:media $response"
      }
      transactionRunner {
        mediaDao.upsert(
          mergeMedia(local = mediaDao.getMediaByIdOrThrow(id), tmdb = response),
        )
      }
    },
    delete = mediaDao::delete,
    deleteAll = mediaDao::deleteAll,
  ),
).build()
