/*
 * Copyright (C) 2021-2026 AfterROOT
 */

package com.afterroot.watchdone.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.tivi.util.Logger
import com.afterroot.watchdone.data.model.OssLibrary
import com.afterroot.watchdone.data.model.UserData
import com.afterroot.watchdone.data.repositories.UserDataRepository
import com.afterroot.watchdone.utils.State
import com.afterroot.watchdone.utils.StateFlowOfState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.Json

@HiltViewModel
class AcknowledgementsViewModel @Inject constructor(
  application: Application,
  json: Json,
  logger: Logger,
  userDataRepository: UserDataRepository,
) : AndroidViewModel(application) {

  val uiState: StateFlowOfState<UserData> = userDataRepository.userData.map {
    State.Success(it)
  }.stateIn(
    scope = viewModelScope,
    initialValue = State.Loading,
    started = SharingStarted.WhileSubscribed(5_000),
  )

  val ossLibraries: StateFlow<List<OssLibrary>> = flow {
    val jsonString = application.resources.assets.open(ACKNOWLEDGEMENTS_FILE_PATH)
      .bufferedReader().use { it.readText() }
    val libraries = json.decodeFromString<List<OssLibrary>>(jsonString)
      .asSequence()
      .distinctBy { "${it.groupId}:${it.artifactId}" }
      .sortedBy { it.name }
      .toList()
    emit(libraries)
  }.catch { e ->
    logger.e(e) { e.message ?: "Error while loading OSS libraries" }
    emit(emptyList())
  }.flowOn(Dispatchers.IO)
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Lazily,
      initialValue = emptyList(),
    )

  companion object {
    private const val ACKNOWLEDGEMENTS_FILE_PATH = "licences/licenses.json"
  }
}
