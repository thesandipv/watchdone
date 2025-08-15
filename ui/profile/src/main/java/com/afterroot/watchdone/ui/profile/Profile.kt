/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import app.tivi.api.UiMessage
import app.tivi.common.compose.Layout
import app.tivi.common.compose.bodyWidth
import app.tivi.common.compose.fullSpanItem
import app.tivi.util.Logger
import com.afterroot.ui.common.compose.components.CommonAppBar
import com.afterroot.ui.common.compose.components.FABEdit
import com.afterroot.ui.common.compose.components.Header
import com.afterroot.ui.common.compose.components.LocalLogger
import com.afterroot.ui.common.compose.components.PosterCard
import com.afterroot.ui.common.compose.theme.buttonShape
import com.afterroot.ui.common.compose.theme.ubuntuTypography
import com.afterroot.ui.common.compose.utils.TopBarWindowInsets
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.data.tmdb.auth.TmdbAuthLoginState
import com.afterroot.watchdone.resources.R
import com.afterroot.watchdone.utils.State
import com.afterroot.watchdone.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun Profile(
  onSignOut: () -> Unit = {},
  onEditProfile: () -> Unit,
  onWatchlistItemClick: (mediaType: MediaType, id: Int) -> Unit,
) {
  Profile(
    viewModel = hiltViewModel(),
    onSignOut = onSignOut,
    onEditProfile = onEditProfile,
    onWatchlistItemClick = onWatchlistItemClick,
  )
}

@Composable
internal fun Profile(
  viewModel: ProfileViewModel,
  logger: Logger = LocalLogger.current,
  onWatchlistItemClick: (mediaType: MediaType, id: Int) -> Unit,
  onSignOut: () -> Unit = {},
  onEditProfile: () -> Unit,
) {
  val scope = rememberCoroutineScope()
  val context = LocalContext.current
  Profile(viewModel = viewModel, onWatchlistItemClick = onWatchlistItemClick) { action ->
    when (action) {
      ProfileActions.SignOut -> {
        logger.d { "Profile: SignOut Start" }
        scope.launch {
          signOut(context).collect { signOutState ->
            logger.d { "Profile: SignOutState: $signOutState" }
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

      ProfileActions.EditProfile -> {
        onEditProfile()
      }

      else -> viewModel.submitAction(action)
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun Profile(
  viewModel: ProfileViewModel,
  onWatchlistItemClick: (mediaType: MediaType, id: Int) -> Unit,
  actions: (ProfileActions) -> Unit,
) {
  val viewState by viewModel.state.collectAsState()

  val watchlist = viewModel.watchlist.collectAsLazyPagingItems()

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(
      NavigationBarDefaults.windowInsets,
    ),
    topBar = {
      CommonAppBar(
        withTitle = stringResource(id = R.string.title_profile),
        windowInsets = TopBarWindowInsets,
        actions = {
          IconButton(onClick = { actions(ProfileActions.SignOut) }) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.Logout,
              contentDescription = stringResource(
                id = R.string.action_sign_out,
              ),
            )
          }
        },
      )
    },
    floatingActionButton = {
      FABEdit(
        modifier = Modifier,
        onClick = {
          actions(ProfileActions.EditProfile)
        },
      )
    },
  ) { paddingValues ->

    var isRefreshing by remember { mutableStateOf(false) }
    val m3pullRefreshState = rememberPullToRefreshState()

    Surface(modifier = Modifier.fillMaxSize()) {
      PullToRefreshBox(
        isRefreshing = isRefreshing,
        modifier = Modifier
          .padding(paddingValues)
          .fillMaxWidth(),
        state = m3pullRefreshState,
        onRefresh = {
          isRefreshing = true
          actions(ProfileActions.Refresh)
          watchlist.refresh()
          isRefreshing = false
        },
      ) {
        val context = LocalContext.current
        ProfileContent(
          profileViewState = viewState,
          watchlist = watchlist,
          modifier = Modifier
            .bodyWidth()
            .fillMaxHeight(),
          onWatchlistItemClick = onWatchlistItemClick,
          onLoginAction = {
            viewModel.startTmdbLoginFlow(context)
          },
          onLogoutAction = {
            viewModel.logoutFromTmdb()
          },
        )
      }
    }
  }
}

@Composable
private fun ProfileContent(
  profileViewState: ProfileViewState,
  watchlist: LazyPagingItems<Media>,
  modifier: Modifier = Modifier,
  onWatchlistItemClick: (mediaType: MediaType, id: Int) -> Unit,
  onLoginAction: () -> Unit,
  onLogoutAction: () -> Unit,
) {
  val listState = rememberLazyGridState()

  val bodyMargin = Layout.bodyMargin
  val gutter = Layout.gutter

  LazyVerticalGrid(
    state = listState,
    columns = GridCells.Fixed(Layout.profileGridColumns),
    contentPadding = PaddingValues(horizontal = bodyMargin, vertical = gutter),
    horizontalArrangement = Arrangement.spacedBy(gutter),
    verticalArrangement = Arrangement.spacedBy(gutter),
    modifier = modifier,
  ) {
    fullSpanItem {
      WatchdoneProfileSection(profileViewState)
    }

    fullHorizontalDivider()

    fullSpanItem {
      TmdbProfileSection(
        profileViewState = profileViewState,
        onLoginAction = onLoginAction,
        onLogoutAction = onLogoutAction,
      )
    }

    fullHorizontalDivider()

    watchlistSection(watchlist, onWatchlistItemClick)
  }
}

@Composable
private fun WatchdoneProfileSection(profileViewState: ProfileViewState) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Header(
      title = "WatchDone Profile",
      spaceAround = 0.dp,
      loading = profileViewState.user is State.Loading,
    )
    if (profileViewState.user is State.Success) {
      val user = profileViewState.user.data
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Column {
          ProvideTextStyle(ubuntuTypography.bodyMedium) {
            Text(user.name.toString())
          }
          ProvideTextStyle(
            ubuntuTypography.bodyMedium.copy(
              color = LocalContentColor.current.copy(alpha = 0.8f),
            ),
          ) {
            Text(user.userName.toString())
          }
        }
        if (profileViewState.wlCount is State.Success) {
          ProvideTextStyle(ubuntuTypography.bodyMedium) {
            val data = profileViewState.wlCount.data
            Text("$data Items")
          }
        }
      }
    }
  }
}

@Composable
private fun TmdbProfileSection(
  modifier: Modifier = Modifier,
  profileViewState: ProfileViewState,
  onLoginAction: () -> Unit,
  onLogoutAction: () -> Unit,
) {
  Column(modifier = modifier) {
    Header(
      title = "TMDb Profile",
      spaceAround = 0.dp,
      loading = profileViewState.tmdbProfile is State.Loading,
    ) {
      if (profileViewState.isTmdbLoggedIn == TmdbAuthLoginState.LOGGED_IN) {
        ButtonTmdbLogout(onClick = onLogoutAction)
      } else {
        ButtonTmdbLogin(onClick = onLoginAction)
      }
    }

    if (profileViewState.isTmdbLoggedIn == TmdbAuthLoginState.LOGGED_IN) {
      if (profileViewState.tmdbProfile is State.Success) {
        profileViewState.tmdbProfile.data.name?.let {
          ProvideTextStyle(ubuntuTypography.bodyMedium) {
            Text(it)
          }
        }

        ProvideTextStyle(
          ubuntuTypography.bodyMedium
            .copy(
              color = LocalContentColor.current.copy(alpha = 0.8f),
            ),
        ) {
          Text(profileViewState.tmdbProfile.data.userName)
        }
      }
      if (profileViewState.tmdbProfile is State.Failed) {
        ProvideTextStyle(
          ubuntuTypography.bodyMedium
            .copy(
              color = LocalContentColor.current.copy(alpha = 0.8f),
            ),
        ) {
          Text(profileViewState.tmdbProfile.message)
        }
      }
    }
  }
}

private fun LazyGridScope.watchlistSection(
  watchlist: LazyPagingItems<Media>,
  onWatchlistItemClick: (mediaType: MediaType, id: Int) -> Unit,
) {
  fullSpanItem {
    Header(
      title = "Watchlist",
      spaceAround = 0.dp,
      loading = watchlist.loadState.refresh == LoadState.Loading,
    )
  }

  if (watchlist.itemCount != 0) {
    items(
      count = watchlist.itemCount,
      key = watchlist.itemKey { item ->
        item.tmdbId ?: item.id
      },
    ) { index ->
      watchlist[index]?.let { item ->
        PosterCard(
          media = item,
          onClick = {
            if (item.mediaType != null) {
              onWatchlistItemClick(item.mediaType!!, item.tmdbId ?: 0)
            }
          },
          modifier = Modifier
            .animateItem(fadeInSpec = null, fadeOutSpec = null)
            .aspectRatio(2 / 3f),
        )
      }
    }
  }

  if (watchlist.loadState.append == LoadState.Loading) {
    fullSpanItem {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
      ) {
        CircularProgressIndicator(Modifier.align(Alignment.Center))
      }
    }
  }
}

@Preview
@Composable
fun PreviewProfileScreen() {
  ButtonTmdbLogin {
  }
}

@Composable
private fun ButtonTmdbLogin(modifier: Modifier = Modifier, onClick: () -> Unit) {
  Button(
    onClick = onClick,
    shape = buttonShape,
    modifier = modifier,
  ) {
    Text("Login")
  }
}

@Composable
private fun ButtonTmdbLogout(modifier: Modifier = Modifier, onClick: () -> Unit) {
  Button(
    onClick = onClick,
    shape = buttonShape,
    modifier = modifier,
  ) {
    Text("Logout")
  }
}

fun LazyGridScope.fullHorizontalDivider(modifier: Modifier = Modifier) {
  fullSpanItem {
    HorizontalDivider(modifier)
  }
}
