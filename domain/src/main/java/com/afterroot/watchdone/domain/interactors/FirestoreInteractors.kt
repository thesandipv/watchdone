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

package com.afterroot.watchdone.domain.interactors

import app.tivi.domain.ResultInteractor
import app.tivi.domain.SubjectInteractor
import com.afterroot.watchdone.data.model.DBMedia
import com.afterroot.watchdone.data.repositories.FirestoreRepository
import com.afterroot.watchdone.utils.State
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WatchlistInteractor @Inject constructor(private val firestoreRepository: FirestoreRepository) :
    ResultInteractor<WatchlistInteractor.Params, Flow<State<Boolean>>>() {
    data class Params(val id: Int, val media: DBMedia = DBMedia.Empty, val method: Method)

    enum class Method {
        ADD, REMOVE, EXIST
    }

    override suspend fun doWork(params: Params): Flow<State<Boolean>> {
        return when (params.method) {
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
}

class WatchStateInteractor @Inject constructor(private val firestoreRepository: FirestoreRepository) :
    ResultInteractor<WatchStateInteractor.Params, Flow<State<Boolean>>>() {
    data class Params(val id: Int, val watchState: Boolean, val episodeId: String? = null, val method: Method)

    enum class Method {
        MEDIA, EPISODE
    }

    override suspend fun doWork(params: Params): Flow<State<Boolean>> {
        return when (params.method) {
            Method.MEDIA -> {
                firestoreRepository.setWatchStatus(params.id, params.watchState)
            }
            Method.EPISODE -> {
                firestoreRepository.setEpisodeWatchStatus(params.id, params.episodeId, params.watchState)
            }
        }
    }
}

class ObserveMediaInfo @Inject constructor(private val firestoreRepository: FirestoreRepository) :
    SubjectInteractor<ObserveMediaInfo.Params, State<DBMedia>>() {
    data class Params(val id: Int)

    override suspend fun createObservable(params: Params): Flow<State<DBMedia>> {
        return firestoreRepository.getMediaInfo(params.id)
    }
}

class MediaInfoInteractor @Inject constructor(private val firestoreRepository: FirestoreRepository) :
    ResultInteractor<MediaInfoInteractor.Params, Flow<State<DBMedia>>>() {
    data class Params(val id: Int)

    override suspend fun doWork(params: Params): Flow<State<DBMedia>> {
        return firestoreRepository.getMediaInfo(params.id)
    }
}
