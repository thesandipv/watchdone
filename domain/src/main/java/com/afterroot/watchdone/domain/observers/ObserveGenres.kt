/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.domain.observers

import app.tivi.domain.SubjectInteractor
import com.afterroot.watchdone.data.model.Genre
import com.afterroot.watchdone.database.dao.GenreDao
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveGenres @Inject constructor(private val genreDao: GenreDao) :
  SubjectInteractor<ObserveGenres.Params, List<Genre>>() {
  data class Params(val ids: List<Int>)

  override suspend fun createObservable(params: Params): Flow<List<Genre>> =
    genreDao.getGenres(params.ids)
}
