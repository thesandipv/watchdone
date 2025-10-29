/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import app.moviebase.tmdb.core.TmdbException
import app.moviebase.tmdb.model.TmdbAccountDetails
import app.tivi.api.UiMessage
import app.tivi.api.UiMessageManager
import app.tivi.domain.invoke
import app.tivi.util.Logger
import com.afterroot.data.utils.FirebaseUtils
import com.afterroot.watchdone.data.mapper.toLocalUser
import com.afterroot.watchdone.data.model.LocalUser
import com.afterroot.watchdone.data.tmdb.account.TmdbAccountActions
import com.afterroot.watchdone.data.tmdb.auth.TmdbAuthRepository
import com.afterroot.watchdone.domain.interactors.GetProfile
import com.afterroot.watchdone.domain.interactors.SetProfile
import com.afterroot.watchdone.domain.interactors.TmdbGetAuthorizationUrl
import com.afterroot.watchdone.domain.interactors.TmdbLogout
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
import org.jetbrains.anko.browse

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
  private val tmdbLogout: TmdbLogout,
  private val tmdbGetAuthorizationUrl: TmdbGetAuthorizationUrl,
  tmdbAuthRepository: TmdbAuthRepository,
  private val tmdbAccountActions: TmdbAccountActions,
) : ViewModel() {

  private val uiMessageManager = UiMessageManager()

  val profile = MutableStateFlow<State<LocalUser>>(State.loading())
  private val tmdbProfile = MutableStateFlow<State<TmdbAccountDetails>>(State.loading())
  private val wlCount = MutableStateFlow<State<Long>>(State.loading())

  val state: StateFlow<ProfileViewState> = combine(
    uiMessageManager.message,
    profile,
    wlCount,
    tmdbAuthRepository.state,
    tmdbProfile,
  ) { msg, profile, wlCount, tmdbLogin, tmdbProfile ->
    ProfileViewState(msg, profile, wlCount, tmdbLogin, tmdbProfile)
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
          else -> logger.d {
            "collectAction: This action not handled by ProfileViewModel. Action: $action"
          }
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

    viewModelScope.launch {
      try {
        tmdbAccountActions.getAccountDetails().also {
          tmdbProfile.value = State.success(it)
        }
      } catch (e: TmdbException) {
        tmdbProfile.value = State.failed(e.message.toString(), exception = e.cause)
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

  fun startTmdbLoginFlow(context: Context) {
    viewModelScope.launch {
      tmdbGetAuthorizationUrl().onSuccess {
        context.browse(it)
      }
    }
  }

  fun logoutFromTmdb() {
    viewModelScope.launch {
      tmdbLogout()
    }
  }
}
