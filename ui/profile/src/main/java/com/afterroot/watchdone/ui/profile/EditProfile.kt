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
package com.afterroot.watchdone.ui.profile

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.tivi.api.UiMessage
import com.afterroot.data.model.NetworkUser
import com.afterroot.data.utils.valueOrBlank
import com.afterroot.ui.common.compose.components.CommonAppBar
import com.afterroot.ui.common.compose.components.FABSave
import com.afterroot.ui.common.compose.components.LocalLogger
import com.afterroot.ui.common.compose.components.OutlinedTextInput
import com.afterroot.ui.common.compose.utils.TopBarWindowInsets
import com.afterroot.watchdone.base.Constants
import com.afterroot.watchdone.data.mapper.toLocalUser
import com.afterroot.watchdone.data.model.LocalUser
import com.afterroot.watchdone.resources.R
import com.afterroot.watchdone.utils.State
import com.afterroot.watchdone.utils.getLocalUser
import com.afterroot.watchdone.viewmodel.ProfileViewModel
import com.firebase.ui.auth.AuthUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber

@Composable
fun EditProfile(
  onSignOut: () -> Unit = {},
  onUpAction: () -> Unit = {},
) {
  EditProfile(viewModel = hiltViewModel(), onSignOut, onUpAction)
}

@Composable
internal fun EditProfile(
  viewModel: ProfileViewModel,
  onSignOut: () -> Unit = {},
  onUpAction: () -> Unit = {},
) {
  val scope = rememberCoroutineScope()
  val context = LocalContext.current
  EditProfile(viewModel = viewModel) { action ->
    when (action) {
      ProfileActions.SignOut -> {
        Timber.d("EditProfile: SignOut Start")
        scope.launch {
          signOut(context).collect { signOutState ->
            Timber.d("EditProfile: SignOutState: $signOutState")
            when (signOutState) {
              is State.Failed -> {
                val showMessage = ProfileActions.ShowMessage(
                  UiMessage("Failed Signing Out."),
                )
                viewModel.submitAction(showMessage)
              }

              is State.Success -> {
                val showMessage = ProfileActions.ShowMessage(
                  UiMessage("Signed Out."),
                )
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

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun EditProfile(
  viewModel: ProfileViewModel,
  actions: (ProfileActions) -> Unit,
) {
  val viewState by viewModel.state.collectAsState()
  val logger = LocalLogger.current
  logger.d { "EditProfile: $viewState" }

  val enteredState = remember { mutableStateOf(LocalUser()) }
  val keyboardController = LocalSoftwareKeyboardController.current

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    contentWindowInsets = WindowInsets.systemBars,
    topBar = {
      CommonAppBar(
        withTitle = stringResource(id = R.string.title_edit_profile),
        windowInsets = TopBarWindowInsets,
        actions = {
          IconButton(onClick = { actions(ProfileActions.SignOut) }) {
            Icon(
              imageVector = Icons.Rounded.Logout,
              contentDescription = stringResource(
                id = R.string.action_sign_out,
              ),
            )
          }
        },
      )
    },
    floatingActionButton = {
      FABSave(
        modifier = Modifier.offset(y = 24.dp),
        onClick = {
          enteredState.value = enteredState.value.trim()
          actions(ProfileActions.SaveProfile(enteredState.value))
        },
      )
    },
  ) { paddingValues ->
    AnimatedVisibility(visible = viewState.user is State.Loading) {
      LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }
    AnimatedVisibility(visible = viewState.user is State.Success) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
          .padding(paddingValues),
      ) {
        val localUser = (viewState.user as State.Success<LocalUser>).data
        OutlinedTextInput(
          modifier = textFieldModifier,
          enabled = localUser.isUserNameAvailable.not(),
          prefillValue = localUser.userName.valueOrBlank(),
          maxLines = 1,
          label = {
            Text("Username") // TODO Replace all hardcoded strings with string resources
          },
          placeholder = {
            Text("Enter desired username")
          },
          onChange = {
            enteredState.value = enteredState.value.copy(userName = it.trim())
          },
          keyboardOptions = KeyboardOptions(
            autoCorrect = false,
            imeAction = ImeAction.Done,
          ),
          validate = {
            when {
              it.length > Constants.USERNAME_LENGTH -> {
                State.failed("Username is too long.")
              }

              it.contains(" ") -> {
                State.failed("Username cannot contain space.")
              }

              else -> State.success(true)
            }
          },
          trailingIcon = {},
        )

        OutlinedTextInput(
          modifier = textFieldModifier,
          prefillValue = localUser.name.valueOrBlank(),
          maxLines = 1,
          label = {
            Text("Name")
          },
          placeholder = {
            Text("Enter full name")
          },
          onChange = {
            enteredState.value = enteredState.value.copy(name = it)
          },
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
          validate = {
            if (it.length > Constants.NAME_LENGTH) {
              State.failed("Name is too long.")
            } else {
              State.success(true)
            }
          },
          onError = {
          },
          trailingIcon = {},
        )
        OutlinedTextInput(
          modifier = textFieldModifier,
          prefillValue = localUser.email.valueOrBlank(),
          maxLines = 1,
          label = {
            Text("Email")
          },
          enabled = false,
          onChange = {
            enteredState.value = enteredState.value.copy(name = it)
          },
          trailingIcon = {},
          keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Email,
          ),
        )
      }
    }
  }
}

val textFieldModifier = Modifier
  .fillMaxWidth()
  .padding(horizontal = 16.dp)
  .padding(top = 8.dp)

@Composable
fun UpdateProfilePrompt() {
  Column(
    modifier = Modifier
      .padding(top = 16.dp)
      .padding(horizontal = 16.dp),
  ) {
    Text(
      text = "Update LocalUser Name",
      textAlign = TextAlign.Center,
      modifier = Modifier.fillMaxWidth(),
    )

    TextField(
      value = "",
      onValueChange = {},
      modifier = Modifier.fillMaxWidth(),
      label = {
        Text(text = "LocalUser Name")
      },
    )
  }
}

@Composable
fun DoWhenUserNameNotAvailable(
  whenNotAvailable: () -> Unit,
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
  content: @Composable (LocalUser) -> Unit = {},
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
      profileViewModel.submitAction(
        ProfileActions.SaveProfile(profileViewModel.firebaseUtils.auth.getLocalUser()),
      )
    }
  }
}

fun signOut(context: Context) = flow {
  emit(State.loading())
  AuthUI.getInstance().signOut(context).await()
  emit(State.success(true))
}.catch {
  emit(State.failed(it.message.toString()))
}.flowOn(Dispatchers.IO)
