/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.domain.interactors

import app.tivi.domain.ResultInteractor
import app.tivi.domain.SubjectInteractor
import com.afterroot.watchdone.data.model.DBMedia
import com.afterroot.watchdone.data.repositories.FirestoreRepository
import com.afterroot.watchdone.utils.State
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class WatchlistInteractor @Inject constructor(
  private val firestoreRepository: FirestoreRepository,
) : ResultInteractor<WatchlistInteractor.Params, Flow<State<Boolean>>>() {
  data class Params(val id: Int, val media: DBMedia = DBMedia.Empty, val method: Method)

  enum class Method {
    ADD,
    REMOVE,
    EXIST,
  }

  override suspend fun doWork(params: Params): Flow<State<Boolean>> = when (params.method) {
    Method.ADD -> {
      firestoreRepository.addToWatchlist(params.media)
    }

    Method.REMOVE -> {
      firestoreRepository.removeFromWatchlist(params.media)
    }

    Method.EXIST -> {
      firestoreRepository.isInWatchlist(params.id)
    }
  }
}

class WatchStateInteractor @Inject constructor(
  private val firestoreRepository: FirestoreRepository,
) : ResultInteractor<WatchStateInteractor.Params, Flow<State<Boolean>>>() {
  data class Params(
    val id: Int,
    val watchState: Boolean,
    val episodeId: String? = null,
    val method: Method,
  )

  enum class Method {
    MEDIA,
    EPISODE,
  }

  override suspend fun doWork(params: Params): Flow<State<Boolean>> = when (params.method) {
    Method.MEDIA -> {
      firestoreRepository.setWatchStatus(params.id, params.watchState)
    }

    Method.EPISODE -> {
      firestoreRepository.setEpisodeWatchStatus(
        params.id,
        params.episodeId,
        params.watchState,
      )
    }
  }
}

class ObserveMediaInfo @Inject constructor(private val firestoreRepository: FirestoreRepository) :
  SubjectInteractor<ObserveMediaInfo.Params, State<DBMedia>>() {
  data class Params(val id: Int)

  override suspend fun createObservable(params: Params): Flow<State<DBMedia>> =
    firestoreRepository.getMediaInfo(params.id)
}

class MediaInfoInteractor @Inject constructor(
  private val firestoreRepository: FirestoreRepository,
) : ResultInteractor<MediaInfoInteractor.Params, Flow<State<DBMedia>>>() {
  data class Params(val id: Int)

  override suspend fun doWork(params: Params): Flow<State<DBMedia>> =
    firestoreRepository.getMediaInfo(params.id)
}

// TODO Add support for different watchlists, currently only default watchlist is supported
class WatchlistCountInteractor @Inject constructor(
  private val firestoreRepository: FirestoreRepository,
) : ResultInteractor<WatchlistCountInteractor.Params, Flow<State<Long>>>() {
  data class Params(val watchlistId: Int)

  override suspend fun doWork(params: Params): Flow<State<Long>> =
    firestoreRepository.getTotalCount()
}
