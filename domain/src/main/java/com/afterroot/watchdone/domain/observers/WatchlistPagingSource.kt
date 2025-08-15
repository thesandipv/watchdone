/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.domain.observers

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.afterroot.data.utils.FirebaseUtils
import com.afterroot.watchdone.base.Collection
import com.afterroot.watchdone.base.Field
import com.afterroot.watchdone.data.mapper.toMedia
import com.afterroot.watchdone.data.model.Filters
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.data.model.WatchStateValues
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.utils.collectionWatchdone
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await
import timber.log.Timber

typealias ExtendedQuery = Query.() -> Query

class WatchlistPagingSource(
  private val firestore: FirebaseFirestore,
  private val settings: Settings,
  private val firebaseUtils: FirebaseUtils,
  private val filters: Filters = Filters.EMPTY,
) : PagingSource<QuerySnapshot, Media>() {
  override fun getRefreshKey(state: PagingState<QuerySnapshot, Media>): QuerySnapshot? = null

  override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Media> =
    try {
      val baseQuery = firestore.collectionWatchdone(
        id = firebaseUtils.uid,
        settings.isUseProdDb,
      ).document(Collection.WATCHLIST).collection(Collection.ITEMS)

      val orderBy: ExtendedQuery = {
        val defaultOrder: ExtendedQuery = {
          orderBy(
            Field.RELEASE_DATE,
            settings.queryDirection,
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
            MediaType.MOVIE -> whereEqualTo(
              Field.MEDIA_TYPE,
              MediaType.MOVIE,
            )

            MediaType.SHOW -> whereEqualTo(
              Field.MEDIA_TYPE,
              MediaType.SHOW,
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
              emptyList<String>(),
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
        val latestSnapshot = baseQuery.limit(1).get().await()
        val latestItem = latestSnapshot.documents.first().toMedia()
        val cachedItem = cachedSnapshot.documents.first().toMedia()

        if (latestItem != cachedItem) {
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
          "load: Data from server: Empty: ${currentPage.isEmpty}, Size: ${currentPage.documents.size}",
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
        data = currentPage.toMedia(),
        prevKey = null,
        nextKey = nextPage,
      )
    } catch (e: Exception) {
      Timber.e(e, "load: ${e.message}")
      LoadResult.Error(e)
    }
}
