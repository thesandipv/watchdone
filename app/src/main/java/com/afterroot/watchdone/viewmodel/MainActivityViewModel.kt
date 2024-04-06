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
package com.afterroot.watchdone.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afterroot.watchdone.data.model.UserData
import com.afterroot.watchdone.data.repositories.UserDataRepository
import com.afterroot.watchdone.utils.FirestoreMigrations
import com.afterroot.watchdone.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MainActivityViewModel @Inject constructor(
  val savedState: SavedStateHandle? = null,
  userDataRepository: UserDataRepository,
  private val firestoreMigrations: FirestoreMigrations,
) : ViewModel() {

  val uiState: StateFlow<State<UserData>> = userDataRepository.userData.map {
    State.success(it)
  }.stateIn(
    scope = viewModelScope,
    initialValue = State.loading(),
    started = SharingStarted.WhileSubscribed(5_000),
  )

  init {
  }

  suspend fun checkForMigrations() {
    firestoreMigrations.start()
  }
}
