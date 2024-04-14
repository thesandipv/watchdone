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

package com.afterroot.watchdone.utils

import app.tivi.util.Logger
import com.afterroot.data.model.WatchlistDocument
import com.afterroot.watchdone.base.Field
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.data.repositories.FirestoreRepository
import com.google.firebase.firestore.FieldValue
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class FirestoreMigrations @Inject constructor(
  private val firestoreRepository: FirestoreRepository,
  private val logger: Logger,
) {
  suspend fun start() {
    val ref = firestoreRepository.watchListRef.get().await()
    when (ref.getLong(Field.VERSION)?.toInt()) {
      0 -> {
        migrateFrom0To1()
      }

      null -> {
        migrateFrom0To1()
      }

      else -> {}
    }
  }

  @Suppress("DEPRECATION")
  private suspend fun migrateFrom0To1() {
    val version = 1

    val watchlistDocumentData = firestoreRepository.watchListRef.get().await()
      .toObject(WatchlistDocument::class.java) ?: WatchlistDocument(0, version)

    logger.d { "Start migrating to version 1" }

    val tvSeriesItems = firestoreRepository.watchlistItemsRef.whereEqualTo(
      Field.MEDIA_TYPE,
      "TV_SERIES",
    ).get().await()
    val watchStatusItems = firestoreRepository.watchlistItemsRef.whereNotEqualTo(
      Field.WATCH_STATUS,
      null,
    ).get().await()

    val batch = firestoreRepository.firestore.batch()

    tvSeriesItems.documents.forEach { snapshot ->
      batch.update(
        firestoreRepository.watchlistItemsRef.document(snapshot.id),
        Field.MEDIA_TYPE,
        MediaType.SHOW,
      )
    }

    watchStatusItems.documents.forEach { snapshot ->
      batch.update(
        firestoreRepository.watchlistItemsRef.document(snapshot.id),
        hashMapOf<String, Any>(
          Field.WATCH_STATUS to FieldValue.delete(),
        ),
      )
    }

    batch.set(firestoreRepository.watchListRef, watchlistDocumentData.copy(version = version))

    batch.commit()

    logger.d { "Migration to version 1 complete" }
  }
}
