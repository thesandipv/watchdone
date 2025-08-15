/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.ui.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afterroot.watchdone.data.model.UserData
import com.afterroot.watchdone.data.repositories.UserDataRepository
import com.afterroot.watchdone.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsActivityViewModel @Inject constructor(
  val savedState: SavedStateHandle? = null,
  private val userDataRepository: UserDataRepository,
) : ViewModel() {

  val uiState: StateFlow<State<UserData>> = userDataRepository.userData.map {
    State.success(it)
  }.stateIn(
    scope = viewModelScope,
    initialValue = State.loading(),
    started = SharingStarted.WhileSubscribed(5_000),
  )

  fun updateSettings(block: suspend UserDataRepository.() -> Unit) {
    viewModelScope.launch {
      userDataRepository.block()
    }
  }
}
