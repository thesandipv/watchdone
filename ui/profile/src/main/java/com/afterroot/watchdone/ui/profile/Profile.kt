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
package com.afterroot.watchdone.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.afterroot.ui.common.compose.components.FABEdit
import com.afterroot.ui.common.compose.components.SwipeDismissSnackbar
import com.afterroot.ui.common.compose.utils.bottomNavigationPadding
import com.afterroot.ui.common.compose.utils.rememberFlowWithLifecycle
import com.afterroot.ui.common.view.UiMessage
import com.afterroot.watchdone.utils.State
import com.afterroot.watchdone.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun Profile(onSignOut: () -> Unit = {}, onEditProfile: () -> Unit) {
    Profile(viewModel = hiltViewModel(), onSignOut, onEditProfile)
}

@Composable
internal fun Profile(viewModel: ProfileViewModel, onSignOut: () -> Unit = {}, onEditProfile: () -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    Profile(viewModel = viewModel) { action ->
        when (action) {
            ProfileActions.SignOut -> {
                Timber.d("Profile: SignOut Start")
                scope.launch {
                    signOut(context).collect { signOutState ->
                        Timber.d("Profile: SignOutState: $signOutState")
                        when (signOutState) {
                            is State.Failed -> {
                                val showMessage = ProfileActions.ShowMessage(UiMessage("Failed Signing Out."))
                                viewModel.submitAction(showMessage)
                            }
                            is State.Success -> {
                                val showMessage = ProfileActions.ShowMessage(UiMessage("Signed Out."))
                                viewModel.submitAction(showMessage)
                                onSignOut()
                            }
                            else -> {
                            }
                        }
                    }
                }
            }
            ProfileActions.EditProfile -> {
                onEditProfile()
            }
            else -> viewModel.submitAction(action)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun Profile(viewModel: ProfileViewModel, actions: (ProfileActions) -> Unit) {
    val scaffoldState = rememberScaffoldState()
    val viewState by rememberFlowWithLifecycle(viewModel.state).collectAsState(initial = ProfileViewState.Empty)

    LaunchedEffect(viewState.message) {
        viewState.message?.let { uiMessage ->
            scaffoldState.snackbarHostState.showSnackbar(uiMessage.message)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        topBar = {
            AppBar(
                title = "Profile",
                actions = {
                    IconButton(
                        onClick = {
                            actions(ProfileActions.SignOut)
                        }
                    ) {
                        Icon(imageVector = Icons.Rounded.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FABEdit(
                modifier = Modifier
                    .bottomNavigationPadding()
                    .navigationBarsPadding(),
                onClick = {
                    actions(ProfileActions.EditProfile)
                }
            )
        },
        snackbarHost = { snackbarHostState ->
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    SwipeDismissSnackbar(
                        data = snackbarData,
                        onDismiss = { viewModel.clearMessage() }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            // TODO
        }
    }
}
