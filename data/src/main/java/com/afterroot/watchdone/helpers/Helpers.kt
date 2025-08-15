/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.helpers

import com.afterroot.watchdone.base.Field
import com.afterroot.watchdone.data.mapper.toDBMedia
import com.afterroot.watchdone.data.model.DBMedia
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.utils.collectionWatchdone
import com.afterroot.watchdone.utils.collectionWatchlistItems
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

/**
 * Migrates old structure to new structure
 * Data Structure changed in version v0.0.4
 * New data structure replaces [Movie] and [TV] with [DBMedia]
 *
 * TODO Remove this check after 2 releases
 *
 * @since v0.0.4
 */
fun migrateFirestore(
  firestore: FirebaseFirestore,
  watchlistSnapshot: QuerySnapshot,
  uid: String,
  isUseProdDb: Boolean,
  successBlock: (() -> Unit)? = null,
) {
  firestore.runBatch { batch ->
    watchlistSnapshot.documents.forEach {
      when (it.getString(Field.MEDIA_TYPE)?.let { type -> MediaType.valueOf(type) }) {
        MediaType.MOVIE -> {
          // Decide whether to run migration by checking value of 'voteAverage' field
          val isRunMigration = it.getDouble("voteAverage") != null
          if (isRunMigration) {
            val movie = it.toObject(Movie::class.java) // Old Structure Object
            val docId = it.id // Remember Document Id
            val media = movie?.toDBMedia() // New Structure Object
            val docRef =
              firestore.collectionWatchdone(
                uid,
                isUseProdDb,
              ).collectionWatchlistItems().document(docId)
            batch.delete(docRef) // First delete the document
            media?.let { dbMedia ->
              batch.set(docRef, dbMedia) // Then set data under same document id
            }
          }
        }

        MediaType.SHOW -> {
          // Decide whether to run migration by checking value of 'voteAverage' field
          val isRunMigration = it.getDouble("voteAverage") != null
          if (isRunMigration) {
            val tv = it.toObject(TV::class.java) // Old Structure Object
            val docId = it.id // Remember Document Id
            val media = tv?.toDBMedia() // New Structure Object
            val docRef =
              firestore.collectionWatchdone(
                uid,
                isUseProdDb,
              ).collectionWatchlistItems().document(docId)
            batch.delete(docRef) // First delete the document
            media?.let { dbMedia ->
              batch.set(docRef, dbMedia) // Then set data under same document id
            }
          }
        }

        else -> {
          // NOT SUPPORTED
        }
      }
    }
  }.addOnSuccessListener {
    successBlock?.invoke()
  }
}

// TODO
suspend fun FirebaseFirestore.filterWatchlist(
  uid: String,
  isUseProdDb: Boolean,
  filter: Query.() -> Query,
): QuerySnapshot =
  collectionWatchdone(uid, isUseProdDb).collectionWatchlistItems().filter().get().await()
