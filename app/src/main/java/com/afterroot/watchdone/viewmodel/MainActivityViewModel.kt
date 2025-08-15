/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
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

  suspend fun checkForMigrations() {
    firestoreMigrations.start()
  }
}
