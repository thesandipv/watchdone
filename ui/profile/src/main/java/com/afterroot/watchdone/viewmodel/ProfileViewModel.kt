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
package com.afterroot.watchdone.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import app.tivi.api.UiMessage
import app.tivi.api.UiMessageManager
import app.tivi.util.Logger
import com.afterroot.data.utils.FirebaseUtils
import com.afterroot.watchdone.data.mapper.toLocalUser
import com.afterroot.watchdone.data.model.LocalUser
import com.afterroot.watchdone.domain.interactors.GetProfile
import com.afterroot.watchdone.domain.interactors.SetProfile
import com.afterroot.watchdone.domain.interactors.WatchlistCountInteractor
import com.afterroot.watchdone.domain.observers.WatchlistPagingSource
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.profile.ProfileActions
import com.afterroot.watchdone.ui.profile.ProfileViewState
import com.afterroot.watchdone.utils.State
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  private val getProfile: GetProfile,
  private val setProfile: SetProfile,
  val firebaseUtils: FirebaseUtils,
  private val settings: Settings,
  private val logger: Logger,
  private var firestore: FirebaseFirestore,
  private val watchlistCountInteractor: WatchlistCountInteractor,
) : ViewModel() {

  private val uiMessageManager = UiMessageManager()

  val profile = MutableStateFlow<State<LocalUser>>(State.loading())
  private val wlCount = MutableStateFlow<State<Long>>(State.loading())

  val state: StateFlow<ProfileViewState> = combine(
    uiMessageManager.message,
    profile,
    wlCount,
  ) { msg, profile, wlCount ->
    ProfileViewState(msg, profile, wlCount)
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = ProfileViewState.Empty,
  )

  private val actions = MutableSharedFlow<ProfileActions>()

  internal fun submitAction(action: ProfileActions) {
    logger.d { "submitAction: $action" }
    viewModelScope.launch {
      actions.emit(action)
    }
  }

  init {
    logger.d { "init: Start" }
    refresh()

    viewModelScope.launch {
      actions.collect { action ->
        when (action) {
          is ProfileActions.SaveProfile -> saveProfileAction(action) { localUser ->
            settings.apply {
              if (userProfile != localUser) {
                userProfile = localUser
                submitAction(ProfileActions.Refresh)
              }
              isUsernameSet = localUser.isUserNameAvailable
            }
          }

          is ProfileActions.ShowMessage -> showMessageAction(action)
          ProfileActions.Refresh -> refresh(true)
          else -> logger.d { "collectAction: This action not handled by ProfileViewModel. Action: $action" }
        }
      }
    }
    viewModelScope.launch {
      watchlistCountInteractor.executeSync(WatchlistCountInteractor.Params(0)).collectLatest {
        wlCount.value = it
      }
    }
  }

  private fun getUserProfile(cached: Boolean = false) {
    viewModelScope.launch {
      if (firebaseUtils.isUserSignedIn) {
        logger.d { "getUserProfile: Getting Profile Info. Cached:$cached" }
        getProfile(firebaseUtils.uid, cached).distinctUntilChanged().map { networkState ->
          when (networkState) {
            is State.Failed -> State.failed(
              message = networkState.message,
              exception = networkState.exception,
            )

            is State.Loading -> State.loading()
            is State.Success -> State.success(networkState.data.toLocalUser())
          }
        }.collect { state ->
          profile.emit(state)
          logger.d { "getUserProfile: State: $state" }
        }
      } else {
        profile.emit(State.failed("Not Signed In."))
      }
    }
  }

  private fun saveProfileAction(action: ProfileActions.SaveProfile, onSave: (LocalUser) -> Unit) {
    viewModelScope.launch {
      if (firebaseUtils.isUserSignedIn) {
        setProfile(firebaseUtils.uid, action.localUser).collect { state ->
          logger.d { "saveProfileAction: $state" }
          when (state) {
            is State.Success -> {
              submitAction(ProfileActions.ShowMessage(UiMessage("Profile Saved.")))
              onSave(action.localUser)
            }

            is State.Failed -> {
              submitAction(
                ProfileActions.ShowMessage(UiMessage("Failed to save Profile")),
              )
            }

            else -> {
            }
          }
        }
      } else {
        submitAction(
          ProfileActions.ShowMessage(
            UiMessage("Failed to save Profile. Please sign in again."),
          ),
        )
      }
    }
  }

  private suspend fun getProfile(uid: String, cached: Boolean = false) =
    getProfile.executeSync(GetProfile.Params(uid, cached))

  private suspend fun setProfile(uid: String, localUser: LocalUser) =
    setProfile.executeSync(SetProfile.Params(uid, localUser))

  private fun refresh(fromUser: Boolean = false) {
    logger.d { "refresh: Start Refresh. From User: $fromUser" }
    getUserProfile()
  }

  private fun showMessageAction(action: ProfileActions.ShowMessage) {
    viewModelScope.launch {
      uiMessageManager.emitMessage(action.message)
    }
  }

  fun clearMessage() {
    viewModelScope.launch {
      uiMessageManager.clearMessage(0)
    }
  }

  val watchlist = Pager(PagingConfig(20)) {
    WatchlistPagingSource(
      firestore,
      settings,
      firebaseUtils,
    )
  }.flow.cachedIn(viewModelScope)
}
