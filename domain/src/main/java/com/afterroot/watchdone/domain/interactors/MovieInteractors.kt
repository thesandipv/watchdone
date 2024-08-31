/*
 * Copyright (C) 2020-2024 Sandip Vaghela
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

package com.afterroot.watchdone.domain.interactors

import app.moviebase.tmdb.model.TmdbCredits
import app.tivi.domain.SubjectInteractor
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.WatchProviderResult
import com.afterroot.watchdone.data.repositories.MovieRepository
import com.afterroot.watchdone.utils.State
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveMovieCredits @Inject constructor(private val movieRepository: MovieRepository) :
  SubjectInteractor<ObserveMovieCredits.Params, State<TmdbCredits>>() {
  data class Params(val movieId: Int)

  override suspend fun createObservable(params: Params): Flow<State<TmdbCredits>> =
    movieRepository.credits(params.movieId)
}

class ObserveMovieInfo @Inject constructor(private val movieRepository: MovieRepository) :
  SubjectInteractor<ObserveMovieInfo.Params, State<Movie>>() {
  data class Params(val movieId: Int)

  override suspend fun createObservable(params: Params): Flow<State<Movie>> =
    movieRepository.info(params.movieId)
}

class ObserveMovieWatchProviders @Inject constructor(private val movieRepository: MovieRepository) :
  SubjectInteractor<ObserveMovieWatchProviders.Params, State<WatchProviderResult>>() {
  data class Params(val id: Int)

  override suspend fun createObservable(params: Params): Flow<State<WatchProviderResult>> =
    movieRepository.watchProviders(params.id)
}
