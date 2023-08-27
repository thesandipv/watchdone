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
package com.afterroot.watchdone.domain.observers

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.afterroot.data.utils.FirebaseUtils
import com.afterroot.watchdone.base.Collection
import com.afterroot.watchdone.base.Field
import com.afterroot.watchdone.data.mapper.toMulti
import com.afterroot.watchdone.data.model.Filters
import com.afterroot.watchdone.data.model.WatchStateValues
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.utils.collectionWatchdone
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import info.movito.themoviedbapi.model.Multi
import kotlinx.coroutines.tasks.await
import timber.log.Timber

typealias ExtendedQuery = Query.() -> Query

class WatchlistPagingSource(
    private val firestore: FirebaseFirestore,
    private val settings: Settings,
    private val firebaseUtils: FirebaseUtils,
    private val filters: Filters = Filters.EMPTY
) : PagingSource<QuerySnapshot, Multi>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, Multi>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Multi> {
        return try {
            val baseQuery = firestore.collectionWatchdone(
                id = firebaseUtils.uid!!,
                settings.isUseProdDb
            )
                .document(Collection.WATCHLIST)
                .collection(Collection.ITEMS)

            val orderBy: ExtendedQuery = {
                val defaultOrder: ExtendedQuery = {
                    orderBy(
                        Field.RELEASE_DATE,
                        settings.queryDirection
                    )
                }
                when (filters.watchState) {
                    WatchStateValues.STARTED -> orderBy(Field.WATCHED_EPISODES).defaultOrder()
                    else -> defaultOrder()
                }
            }

            val filterBy: ExtendedQuery = {
                val mediaTypeFilter: ExtendedQuery = {
                    when (filters.mediaType) {
                        Multi.MediaType.MOVIE -> whereEqualTo(
                            Field.MEDIA_TYPE,
                            Multi.MediaType.MOVIE.name
                        )
                        Multi.MediaType.TV_SERIES -> whereEqualTo(
                            Field.MEDIA_TYPE,
                            Multi.MediaType.TV_SERIES.name
                        )
                        else -> this
                    }
                }

                val statusFilter: ExtendedQuery = {
                    when (filters.watchState) {
                        WatchStateValues.WATCHED -> whereEqualTo(Field.IS_WATCHED, true)
                        WatchStateValues.PENDING -> whereIn(Field.IS_WATCHED, listOf(false, null))
                        WatchStateValues.STARTED -> whereNotEqualTo(
                            Field.WATCHED_EPISODES,
                            emptyList<String>()
                        )
                        else -> this
                    }
                }

                mediaTypeFilter().statusFilter()
            }

            var currentPageSource = Source.CACHE
            val nextPageSource = Source.DEFAULT

            val cachedSnapshot = baseQuery.limit(3).get(Source.CACHE).await()

            if (!cachedSnapshot.isEmpty && cachedSnapshot.size() > 2) {
                val latestPointerSnapshot = baseQuery.limit(1).get().await()
                val latestPointer = latestPointerSnapshot.documents.first().toMulti()
                val cachedPointer = cachedSnapshot.documents.first().toMulti()

                if (latestPointer != cachedPointer) {
                    currentPageSource = Source.DEFAULT
                }
            } else {
                currentPageSource = Source.DEFAULT
            }

            var currentPage: QuerySnapshot = params.key ?: baseQuery.orderBy().filterBy()
                .limit(20).get(currentPageSource).await()

            if (currentPage.isEmpty && currentPageSource == Source.CACHE) {
                Timber.d("load: Cache is empty. Getting data from Server.")
                currentPage = params.key ?: baseQuery.orderBy().filterBy().limit(20).get().await()
                Timber.d(
                    "load: Data from server: Empty: ${currentPage.isEmpty}, Size: ${currentPage.documents.size}"
                )
            }

            var nextPage: QuerySnapshot? = null

            if (!currentPage.isEmpty) {
                val nextPageQuery = baseQuery
                    .limit(15).orderBy().filterBy()
                    .startAfter(currentPage.documents.last())

                nextPage = nextPageQuery.get(nextPageSource).await()

                if (nextPage?.isEmpty != true) {
                    if (currentPage.documents.last() == nextPage?.documents?.first()) {
                        Timber.d("load: No more data. End of list.")
                        nextPage = null
                    }
                }
            }

            LoadResult.Page(
                data = currentPage.toMulti(),
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            Timber.e(e, "load: ${e.message}")
            LoadResult.Error(e)
        }
    }
}
