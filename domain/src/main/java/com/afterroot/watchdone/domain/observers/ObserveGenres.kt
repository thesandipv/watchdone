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

import app.tivi.domain.SubjectInteractor
import com.afterroot.tmdbapi.model.Genre
import com.afterroot.watchdone.database.GenreDao
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveGenres @Inject constructor(private val genreDao: GenreDao) :
    SubjectInteractor<ObserveGenres.Params, List<Genre>>() {
    data class Params(val ids: List<Int>)

    override suspend fun createObservable(params: Params): Flow<List<Genre>> {
        return genreDao.getGenres(params.ids)
    }
}
