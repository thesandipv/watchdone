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
import com.afterroot.watchdone.utils.resultFlow
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class FirestoreRepository @Inject constructor(
  val firestore: FirebaseFirestore,
  settings: Settings,
  firebaseUtils: FirebaseUtils,
) {

  val watchListRef by lazy {
    firestore.collectionWatchdone(
      id = firebaseUtils.uid.toString(),
      isUseOnlyProdDB = settings.isUseProdDb,
    ).documentWatchlist()
  }

  val watchlistItemsRef by lazy {
    watchListRef.collectionWatchlistItems()
  }

  fun addToWatchlist(media: DBMedia) = resultFlow {
    require(media != DBMedia.Empty) {
      "DBMedia should not be Empty"
    }
    watchlistItemsRef.add(media).await()
    watchListRef.updateTotalItemsCounter(1)
    emit(State.success(true))
  }

  fun removeFromWatchlist(media: DBMedia) = resultFlow {
    val documentId = getDocumentId(media)
    if (documentId != null) {
      watchlistItemsRef.document(documentId).delete().await()
      watchListRef.updateTotalItemsCounter(-1)
      emit(State.success(false))
    } else {
      emit(State.failed("Media not found"))
    }
  }

  fun isInWatchlist(mediaId: Int) = resultFlow {
    emit(State.success(getDocumentId(mediaId) != null))
  }

  // WatchStatus should only be changed if media is present in watchlist
  fun setWatchStatus(mediaId: Int, isWatched: Boolean) = resultFlow {
    val documentId = getDocumentId(mediaId)
    if (documentId != null) {
      watchlistItemsRef.document(documentId).update(Field.IS_WATCHED, isWatched).await()
      emit(State.success(isWatched))
    } else {
      emit(State.failed("Media not found"))
    }
  }

  // EpisodeWatchStatus should only be changed if media is present in watchlist
  fun setEpisodeWatchStatus(tvId: Int, episodeId: String?, isWatched: Boolean) = resultFlow {
    require(episodeId != null) {
      "EpisodeID cannot be null"
    }
    val documentId = getDocumentId(tvId)
    if (documentId != null) {
      watchlistItemsRef.document(documentId).update(
        Field.WATCHED_EPISODES,
        if (isWatched) {
          FieldValue.arrayUnion(episodeId)
        } else {
          FieldValue.arrayRemove(episodeId)
        },
      ).await()
      emit(State.success(isWatched))
    } else {
      emit(State.failed("Media not found"))
    }
  }

  fun getMediaInfo(mediaId: Int) = resultFlow {
    val media = getDocumentId(mediaId)?.let {
      watchlistItemsRef.document(it).get().await().toObject(DBMedia::class.java)
    }
    if (media != null) {
      emit(State.success(media))
    } else {
      emit(State.failed("Media not found"))
    }
  }

  private suspend fun getDocumentId(media: DBMedia, source: Source = Source.CACHE) =
    getDocumentId(
      media.id,
      source,
    )

  private suspend fun getDocumentId(mediaId: Int, source: Source = Source.CACHE): String? {
    val qs = watchlistItemsRef.whereEqualTo(Field.ID, mediaId).get(source).await()
    return if (qs.documents.size > 0) qs.documents[0].id else null
  }

  private fun DocumentReference.updateTotalItemsCounter(
    by: Long,
    doOnSuccess: (() -> Unit)? = null,
  ) {
    this.set(
      hashMapOf(Field.TOTAL_ITEMS to FieldValue.increment(by)),
      SetOptions.merge(),
    ).addOnCompleteListener {
      doOnSuccess?.invoke()
    }
  }
}
