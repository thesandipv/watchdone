/*
 * 2021 AfterROOT
 */
package com.afterroot.watchdone.ui.profile

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.afterroot.data.model.NetworkUser
import com.afterroot.data.utils.valueOrBlank
import com.afterroot.ui.common.compose.components.CommonAppBar
import com.afterroot.ui.common.compose.components.FABSave
import com.afterroot.ui.common.compose.components.SwipeDismissSnackbar
import com.afterroot.ui.common.compose.components.UpActionButton
import com.afterroot.ui.common.compose.theme.appBarTitleStyle
import com.afterroot.ui.common.compose.utils.bottomNavigationPadding
import com.afterroot.ui.common.compose.utils.rememberFlowWithLifecycle
import com.afterroot.ui.common.view.UiMessage
import com.afterroot.watchdone.base.Constants
import com.afterroot.watchdone.data.mapper.toLocalUser
import com.afterroot.watchdone.data.model.LocalUser
import com.afterroot.watchdone.utils.State
import com.afterroot.watchdone.utils.getLocalUser
import com.afterroot.watchdone.utils.logD
import com.afterroot.watchdone.viewmodel.ProfileViewModel
import com.firebase.ui.auth.AuthUI
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun EditProfile(
    standalone: Boolean = false,
    onSignOut: () -> Unit = {},
    onUpAction: () -> Unit = {}
) {
    EditProfile(viewModel = hiltViewModel(), standalone, onSignOut, onUpAction)
}

@Composable
internal fun EditProfile(
    viewModel: ProfileViewModel,
    standalone: Boolean = false,
    onSignOut: () -> Unit = {},
    onUpAction: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    EditProfile(viewModel = viewModel, standalone) { action ->
        when (action) {
            ProfileActions.SignOut -> {
                logD("EditProfile/SignOut", "Start")
                scope.launch {
                    signOut(context).collect { signOutState ->
                        logD("EditProfile/SignOut", "SignOutState: $signOutState")
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
            ProfileActions.Up -> {
                onUpAction()
            }
            else -> viewModel.submitAction(action)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun EditProfile(viewModel: ProfileViewModel, standalone: Boolean = false, actions: (ProfileActions) -> Unit) {
    val profileState = viewModel.profile.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val viewState by rememberFlowWithLifecycle(viewModel.state).collectAsState(initial = ProfileViewState.Empty)
    val enteredState = remember { mutableStateOf(LocalUser()) }
    val keyboardController = LocalSoftwareKeyboardController.current
    // val networkUser = remember { mutableStateOf(NetworkUser()) }

    LaunchedEffect(profileState.value) {
        if (profileState.value is State.Success) {
            enteredState.value = (profileState.value as State.Success<NetworkUser>).data.toLocalUser()
        }
    }

    LaunchedEffect(viewState.message) {
        viewState.message?.let { error ->
            scaffoldState.snackbarHostState.showSnackbar(error.message)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        topBar = {
            Box(
                modifier = Modifier.statusBarsPadding(standalone)
            ) {
                AppBar(
                    getTitle(standalone),
                    actions = {
                        if (!standalone) {
                            // logout button will not be added if screen is standalone
                            IconButton(
                                onClick = {
                                    actions(ProfileActions.SignOut)
                                }
                            ) {
                                Icon(imageVector = Icons.Rounded.Logout, contentDescription = "Back")
                            }
                        }
                    },
                    navigationIcon = {
                        if (!standalone) {
                            UpActionButton {
                                actions(ProfileActions.Up)
                            }
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FABSave(
                modifier = Modifier
                    .bottomNavigationPadding(standalone)
                    .navigationBarsPadding(),
                onClick = {
                    enteredState.value = enteredState.value.trim()
                    actions(ProfileActions.SaveProfile(enteredState.value))
                    logD("NewPost", "Clicked Save")
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
            TextField(
                modifier = textFieldModifier,
                enabled = enteredState.value.isUserNameAvailable.not(),
                value = enteredState.value.userName.valueOrBlank(),
                maxLines = 1,
                label = {
                    Text("Username") // TODO Replace all hardcoded strings with string resources
                },
                placeholder = {
                    Text("Enter desired username")
                },
                onValueChange = {
                    if (it.length < Constants.USERNAME_LENGTH) {
                        enteredState.value = enteredState.value.copy(userName = it.trim())
                    }
                },
                keyboardOptions = KeyboardOptions(autoCorrect = false, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                )
            )
            TextField(
                modifier = textFieldModifier,
                value = enteredState.value.name.valueOrBlank(),
                maxLines = 1,
                label = {
                    Text("Name")
                },
                placeholder = {
                    Text("Enter full name")
                },
                onValueChange = {
                    if (it.length < Constants.NAME_LENGTH) {
                        enteredState.value = enteredState.value.copy(name = it)
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                )
            )
            TextField(
                modifier = textFieldModifier,
                value = enteredState.value.email.valueOrBlank(),
                maxLines = 1,
                label = {
                    Text("Email")
                },
                enabled = false,
                onValueChange = {
                }
            )
        }
    }
}

val textFieldModifier = Modifier
    .fillMaxWidth()
    .padding(horizontal = 16.dp)
    .padding(top = 8.dp)

@Composable
internal fun AppBar(
    title: String = getTitle(),
    actions: @Composable RowScope.() -> Unit = {},
    navigationIcon: @Composable (() -> Unit)? = null
) {
    CommonAppBar(withTitle = title, actions = actions, navigationIcon = navigationIcon)
}

@Composable
fun UpdateProfilePrompt() {
    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .padding(horizontal = 16.dp)
    ) {
        CompositionLocalProvider(
            LocalContentAlpha provides ContentAlpha.high,
            content = {
                Text(
                    text = "Update LocalUser Name",
                    textAlign = TextAlign.Center,
                    style = appBarTitleStyle,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )

        TextField(
            value = "", onValueChange = {}, modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "LocalUser Name")
            }
        )
    }
}

@Composable
fun DoWhenUserNameNotAvailable(
    whenNotAvailable: () -> Unit
) {
    UserProfile { profile ->
        if (profile.userName == null || !profile.isUserNameAvailable) {
            whenNotAvailable()
        }
    }
}

@Composable
fun UserProfile(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    loadingContent: @Composable () -> Unit = {},
    content: @Composable (LocalUser) -> Unit = {}
) {
    val profileState = profileViewModel.profile.collectAsState(State.loading())
    when (profileState.value) {
        is State.Loading -> {
            loadingContent()
        }
        is State.Success -> {
            val localUser = (profileState.value as State.Success<NetworkUser>).data.toLocalUser()
            content(localUser)
        }
        is State.Failed -> {
            profileViewModel.submitAction(ProfileActions.SaveProfile(profileViewModel.firebaseUtils.auth.getLocalUser()))
        }
    }
}

fun Modifier.navigationBarsPadding(standalone: Boolean) = if (standalone) {
    navigationBarsPadding()
} else {
    this
}

fun Modifier.bottomNavigationPadding(standalone: Boolean) = if (standalone) {
    this
} else {
    bottomNavigationPadding()
}

fun Modifier.statusBarsPadding(standalone: Boolean) = if (standalone) {
    statusBarsPadding()
} else {
    this
}

fun getTitle(standalone: Boolean = false) = if (standalone) "Update Profile" else "Edit Profile"

fun signOut(context: Context) = flow {
    emit(State.loading())
    AuthUI.getInstance().signOut(context).await()
    emit(State.success(true))
}.catch {
    emit(State.failed(it.message.toString()))
}.flowOn(Dispatchers.IO)
