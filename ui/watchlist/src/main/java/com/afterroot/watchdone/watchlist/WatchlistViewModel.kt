/*
 * Copyright (C) 2020-2022 Sandip Vaghela
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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.afterroot.data.utils.FirebaseUtils
import com.afterroot.watchdone.base.Collection
import com.afterroot.watchdone.data.QueryAction
import com.afterroot.watchdone.data.WatchlistPagingSource
import com.afterroot.watchdone.data.mapper.toMulti
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.utils.collectionWatchdone
import com.afterroot.watchdone.viewmodel.ViewModelState
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import info.movito.themoviedbapi.model.Multi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    var db: FirebaseFirestore,
    var settings: Settings,
    var firebaseUtils: FirebaseUtils
) : ViewModel() {
    private val uid: String = firebaseUtils.uid.toString()
    private val actions = MutableSharedFlow<WatchlistActions>()
    val uiActions = MutableSharedFlow<WatchlistActions>()
    val flowIsLoading = MutableStateFlow(false)

    val state: StateFlow<WatchlistState> = combine(flowIsLoading) { isLoading ->
        WatchlistState(isLoading[0])
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WatchlistState.INITIAL
    )

    init {
        Timber.d("init: Initializing")

        viewModelScope.launch {
            actions.collect { action ->
                Timber.d("actions: Collected action: $action")
                when (action) {
                    is WatchlistActions.SetQueryAction -> {
                        savedStateHandle["QUERY_ACTION"] = action.queryAction.name
                    }
                    else -> {
                        viewModelScope.launch {
                            uiActions.emit(action)
                        }
                    }
                }
            }
        }
    }

    fun submitAction(action: WatchlistActions) {
        viewModelScope.launch {
            actions.emit(action)
        }
    }

    @Deprecated("Use WatchlistPagingSource instead")
    fun getWatchlistSnapshot(userId: String = uid): Flow<ViewModelState> = callbackFlow {
        val ref = db.collectionWatchdone(id = userId, settings.isUseProdDb)
            .document(Collection.WATCHLIST)
            .collection(Collection.ITEMS)
        val subs = ref.addSnapshotListener { value, error ->
            if (value == null) return@addSnapshotListener
            try {
                trySend(ViewModelState.Loaded(value)).isSuccess
            } catch (e: Throwable) {
            }
        }

        awaitClose { subs.remove() }
    }

    @Deprecated("Use WatchlistPagingSource instead")
    fun getWatchlistItems(userId: String = uid): Flow<List<Multi>> = callbackFlow {
        val ref = db.collectionWatchdone(id = userId, settings.isUseProdDb)
            .document(Collection.WATCHLIST)
            .collection(Collection.ITEMS)
        val subs = ref.addSnapshotListener { value, _ ->
            if (value == null) return@addSnapshotListener
            try {
                this.trySend(value.toMulti()).isSuccess
            } catch (e: Throwable) {
            }
        }

        awaitClose { subs.remove() }
    }

    val watchlist = Pager(PagingConfig(20)) {
        WatchlistPagingSource(
            db,
            settings,
            firebaseUtils,
            QueryAction.valueOf(savedStateHandle.get<String>("QUERY_ACTION") ?: QueryAction.CLEAR.name)
        )
    }.flow.cachedIn(viewModelScope)
}
