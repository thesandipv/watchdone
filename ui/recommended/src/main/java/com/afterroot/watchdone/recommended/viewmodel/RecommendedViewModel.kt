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
package com.afterroot.watchdone.recommended.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.afterroot.watchdone.data.model.TV
import com.afterroot.watchdone.domain.interactors.UpdateRecommendedShows
import com.afterroot.watchdone.domain.observers.RecommendedShowPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class RecommendedViewModel @Inject constructor(
    val updateRecommendedShows: UpdateRecommendedShows
) : ViewModel() {
    val recommendedShows: Flow<PagingData<TV>> = Pager(PagingConfig(pageSize = 10, initialLoadSize = 30)) {
        RecommendedShowPagingSource(updateRecommendedShows)
    }.flow.cachedIn(viewModelScope)
}