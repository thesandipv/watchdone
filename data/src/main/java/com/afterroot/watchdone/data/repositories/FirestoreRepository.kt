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

package com.afterroot.watchdone.data.repositories

import com.afterroot.data.utils.FirebaseUtils
import com.afterroot.watchdone.base.Field
import com.afterroot.watchdone.data.model.DBMedia
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.utils.State
import com.afterroot.watchdone.utils.collectionWatchdone
import com.afterroot.watchdone.utils.collectionWatchlistItems
import com.afterroot.watchdone.utils.documentWatchlist
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreRepository @Inject constructor(
    val firestore: FirebaseFirestore,
    settings: Settings,
    firebaseUtils: FirebaseUtils
) {

    private val watchListRef by lazy {
        firestore.collectionWatchdone(
            id = firebaseUtils.uid.toString(),
            isUseOnlyProdDB = settings.isUseProdDb
        ).documentWatchlist()
    }

    private val watchlistItemsRef by lazy {
        watchListRef.collectionWatchlistItems()
    }

    fun addToWatchlist(media: DBMedia) = flow {
        emit(State.loading())
        watchlistItemsRef.add(media).await()
        watchListRef.updateTotalItemsCounter(1)
        emit(State.success(true))
    }.catch { exception ->
        emit(State.failed(exception.message.toString()))
    }

    fun removeFromWatchlist(media: DBMedia) = flow<State<Boolean>> {
        getDocumentId(media)?.let {
            watchlistItemsRef.document(it).delete().await()
            watchListRef.updateTotalItemsCounter(-1)
            emit(State.success(false))
        }
    }.catch { exception ->
        emit(State.failed(exception.message.toString()))
    }

    fun isInWatchlist(mediaId: Int) = flow {
        emit(State.loading())

        if (getDocumentId(mediaId) == null) {
            emit(State.success(false))
        } else {
            emit(State.success(true))
        }
    }.catch { exception ->
        emit(State.failed(exception.message.toString()))
    }

    fun setWatchStatus(mediaId: Int, isWatched: Boolean) = flow<State<Boolean>> {
        getDocumentId(mediaId)?.let {
            watchlistItemsRef.document(it).update(Field.IS_WATCHED, isWatched).await()
            emit(State.success(isWatched))
        }
    }.catch { exception ->
        emit(State.failed(exception.message.toString(), exception = exception))
    }

    fun setEpisodeWatchStatus(tvId: Int, episodeId: String?, isWatched: Boolean) = flow<State<Boolean>> {
        require(episodeId != null) {
            "EpisodeID cannot be null"
        }
        getDocumentId(tvId)?.let {
            watchlistItemsRef.document(it)
                .update("${Field.WATCH_STATUS}.$episodeId", isWatched).await()
            emit(State.success(isWatched))
        }
    }.catch { exception ->
        emit(State.failed(exception.message.toString(), exception = exception))
    }

    fun getMediaInfo(mediaId: Int) = flow<State<DBMedia>> {
        getDocumentId(mediaId)?.let {
            val media = watchlistItemsRef.document(it).get().await().toObject(DBMedia::class.java)
            if (media != null) {
                emit(State.success(media))
            } else {
                emit(State.failed("Media not found"))
            }
        }
    }.catch { exception ->
        emit(State.failed(exception.message.toString(), exception = exception))
    }

    private suspend fun getDocumentId(media: DBMedia, source: Source = Source.CACHE) = getDocumentId(media.id, source)

    private suspend fun getDocumentId(mediaId: Int, source: Source = Source.CACHE): String? {
        val qs = watchlistItemsRef.whereEqualTo(Field.ID, mediaId).get(source).await()
        return if (qs.documents.size > 0) qs.documents[0].id else null
    }

    private fun DocumentReference.updateTotalItemsCounter(by: Long, doOnSuccess: (() -> Unit)? = null) {
        this.set(hashMapOf(Field.TOTAL_ITEMS to FieldValue.increment(by)), SetOptions.merge()).addOnCompleteListener {
            doOnSuccess?.invoke()
        }
    }
}
