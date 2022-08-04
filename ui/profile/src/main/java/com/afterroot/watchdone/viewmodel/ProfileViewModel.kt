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
package com.afterroot.watchdone.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afterroot.data.model.NetworkUser
import com.afterroot.data.utils.FirebaseUtils
import com.afterroot.ui.common.view.SnackbarManager
import com.afterroot.ui.common.view.UiMessage
import com.afterroot.watchdone.data.model.LocalUser
import com.afterroot.watchdone.domain.interactors.GetProfile
import com.afterroot.watchdone.domain.interactors.SetProfile
import com.afterroot.watchdone.settings.Settings
import com.afterroot.watchdone.ui.profile.ProfileActions
import com.afterroot.watchdone.ui.profile.ProfileViewState
import com.afterroot.watchdone.utils.State
import com.afterroot.watchdone.utils.logD
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val snackbarManager: SnackbarManager,
    private val getProfile: GetProfile,
    private val setProfile: SetProfile,
    val firebaseUtils: FirebaseUtils,
    private val settings: Settings
) : ViewModel() {

    val state: StateFlow<ProfileViewState> = combine(snackbarManager.messages) { msg ->
        ProfileViewState(msg[0])
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileViewState.Empty
    )

    val profile = MutableStateFlow<State<NetworkUser>>(State.loading())

    private val actions = MutableSharedFlow<ProfileActions>()
    internal fun getAction(): SharedFlow<ProfileActions> = actions

    internal fun submitAction(action: ProfileActions) {
        logD("ProfileViewModel/submitAction", "Action: $action")
        viewModelScope.launch {
            actions.emit(action)
        }
    }

    init {
        logD("ProfileViewModel/Init", "Init ViewModel")
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
                    else -> logD(
                        "ProfileViewModel/submitAction",
                        "This action not handled by ProfileViewModel. Action: $action"
                    )
                }
            }
        }
    }

    private fun getUserProfile(cached: Boolean = false) {
        viewModelScope.launch {
            if (firebaseUtils.isUserSignedIn) {
                logD("ProfileViewModel/getUserProfile", "Getting Profile Info. Cached:$cached")
                getProfile(firebaseUtils.uid!!, cached).distinctUntilChanged().collect { state ->
                    profile.emit(state)
                    logD("ProfileViewModel/getUserProfile", "State: $state")
                }
            } else {
                profile.emit(State.failed("Not Signed In."))
            }
        }
    }

    private fun saveProfileAction(action: ProfileActions.SaveProfile, onSave: (LocalUser) -> Unit) {
        viewModelScope.launch {
            if (firebaseUtils.isUserSignedIn) {
                setProfile(firebaseUtils.uid!!, action.localUser).collect { state ->
                    logD("ProfileViewModel/setUserProfile", "$state")
                    when (state) {
                        is State.Success -> {
                            submitAction(ProfileActions.ShowMessage(UiMessage("Profile Saved.")))
                            onSave(action.localUser)
                        }
                        is State.Failed -> {
                            submitAction(ProfileActions.ShowMessage(UiMessage("Failed to save Profile")))
                        }
                        else -> {
                        }
                    }
                }
            } else {
                submitAction(ProfileActions.ShowMessage(UiMessage("Failed to save Profile. Please sign in again.")))
            }
        }
    }

    private suspend fun getProfile(uid: String, cached: Boolean = false) =
        getProfile.executeSync(GetProfile.Params(uid, cached))

    private suspend fun setProfile(uid: String, localUser: LocalUser) =
        setProfile.executeSync(SetProfile.Params(uid, localUser))

    private fun refresh(fromUser: Boolean = false) {
        logD("ProfileViewModel/Refresh", "Start Refresh. From User: $fromUser")
        getUserProfile()
    }

    private fun showMessageAction(action: ProfileActions.ShowMessage) {
        viewModelScope.launch {
            snackbarManager.addMessage(action.message)
        }
    }

    fun clearMessage() {
        viewModelScope.launch {
            snackbarManager.removeCurrentMessage()
        }
    }
}
