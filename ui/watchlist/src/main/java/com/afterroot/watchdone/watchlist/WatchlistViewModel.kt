/*
 * Copyright (C) 2020-2021 Sandip Vaghela
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
package com.afterroot.watchdone.watchlist

import androidx.lifecycle.ViewModel
import com.afterroot.tmdbapi.model.Multi
import com.afterroot.watchdone.base.Collection
import com.afterroot.watchdone.data.mapper.toMulti
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.utils.FirebaseUtils
import com.afterroot.watchdone.utils.collectionWatchdone
import com.afterroot.watchdone.viewmodel.ViewModelState
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WatchlistViewModel : ViewModel(), KoinComponent {
    private val db: FirebaseFirestore by inject()
    val settings: Settings by inject()
    val watchlistSnapshot = MutableSharedFlow<ViewModelState>()
    val firebaseUtils: FirebaseUtils by inject()
    val uid: String = firebaseUtils.uid.toString()

    fun getWatchlistSnapshot(userId: String = uid): Flow<ViewModelState> = callbackFlow {
        val ref = db.collectionWatchdone(id = userId, settings.isUseProdDb)
            .document(Collection.WATCHLIST)
            .collection(Collection.ITEMS)
        val subs = ref.addSnapshotListener { value, error ->
            if (value == null) return@addSnapshotListener
            try {
                offer(ViewModelState.Loaded(value))
            } catch (e: Throwable) {
            }
        }

        awaitClose { subs.remove() }
    }

    fun getWatchlistItems(userId: String = uid): Flow<List<Multi>> = callbackFlow {
        val ref = db.collectionWatchdone(id = userId, settings.isUseProdDb)
            .document(Collection.WATCHLIST)
            .collection(Collection.ITEMS)
        val subs = ref.addSnapshotListener { value, _ ->
            if (value == null) return@addSnapshotListener
            try {
                offer(value.toMulti())
            } catch (e: Throwable) {
            }
        }

        awaitClose { subs.remove() }
    }
}
