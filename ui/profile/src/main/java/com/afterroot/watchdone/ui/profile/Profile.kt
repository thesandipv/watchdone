/*
 * 2021 AfterROOT
 */
package com.afterroot.watchdone.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
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
import com.afterroot.watchdone.utils.logD
import com.afterroot.watchdone.viewmodel.ProfileViewModel
import com.google.accompanist.insets.navigationBarsPadding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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
                logD("Profile/SignOut", "Start")
                scope.launch {
                    signOut(context).collect { signOutState ->
                        logD("Profile/SignOut", "SignOutState: $signOutState")
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

@OptIn(ExperimentalComposeUiApi::class)
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
                    logD("Profile", "Action: Edit Profile")
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
