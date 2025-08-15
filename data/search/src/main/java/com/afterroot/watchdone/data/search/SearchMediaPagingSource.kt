/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.afterroot.watchdone.data.model.Media
import java.io.IOException

class SearchMediaPagingSource(
  private val searchParams: SearchDataSource.Params,
  private val searchRepository: SearchRepository,
) : PagingSource<Int, Media>() {
  override fun getRefreshKey(state: PagingState<Int, Media>): Int? = null

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Media> = try {
    val pageNumber = params.key ?: 1
    val response = searchRepository.search(searchParams.copy(page = pageNumber))

    val nextKey = if (response.isNotEmpty()) pageNumber + 1 else null

    LoadResult.Page(data = response, prevKey = null, nextKey = nextKey)
  } catch (e: IOException) {
    LoadResult.Error(e)
  } catch (e: Exception) {
    LoadResult.Error(e)
  }
}
