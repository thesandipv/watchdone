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

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import com.afterroot.ui.common.compose.components.CommonAppBar
import com.afterroot.ui.common.compose.components.FABEdit
import com.afterroot.ui.common.compose.components.Header
import com.afterroot.ui.common.compose.components.PosterCard
import com.afterroot.ui.common.compose.theme.ubuntuTypography
import com.afterroot.ui.common.compose.utils.TopBarWindowInsets
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.resources.R
import com.afterroot.watchdone.utils.State
import com.afterroot.watchdone.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun Profile(onSignOut: () -> Unit = {}, onEditProfile: () -> Unit) {
  Profile(viewModel = hiltViewModel(), onSignOut, onEditProfile)
}

@Composable
internal fun Profile(
  viewModel: ProfileViewModel,
  onSignOut: () -> Unit = {},
  onEditProfile: () -> Unit,
) {
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

@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalMaterialApi::class,
)
@Composable
internal fun Profile(viewModel: ProfileViewModel, actions: (ProfileActions) -> Unit) {
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

    val refreshState = rememberPullRefreshState(
      refreshing = watchlist.loadState.refresh == LoadState.Loading,
      onRefresh = {
        watchlist.refresh()
      },
    )

    Box(
      modifier = Modifier
        .padding(paddingValues)
        .pullRefresh(state = refreshState)
        .fillMaxWidth(),
    ) {
      if (watchlist.itemCount != 0) {
        ProfileWatchlistSection(profileViewState = viewState, watchlist = watchlist)
      }

      PullRefreshIndicator(
        refreshing = watchlist.loadState.refresh == LoadState.Loading,
        state = refreshState,
        modifier = Modifier
          .align(Alignment.TopCenter),
        scale = true,
      )
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileWatchlistSection(
  profileViewState: ProfileViewState,
  watchlist: LazyPagingItems<Media>,
  modifier: Modifier = Modifier,
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
    modifier = Modifier
      .bodyWidth()
      .fillMaxHeight(),
  ) {
    fullSpanItem {
      ProfileTitle(profileViewState)
    }

    fullSpanItem {
      HorizontalDivider()
    }

    fullSpanItem {
      Header(
        title = "Watchlist",
        spaceAround = 0.dp,
        loading = watchlist.loadState.refresh == LoadState.Loading,
      )
    }

    items(
      count = watchlist.itemCount,
      key = watchlist.itemKey { item ->
        item.tmdbId ?: item.id
      },
    ) { index ->
      watchlist[index]?.let { item ->
        PosterCard(
          media = item,
          onClick = { },
          modifier = Modifier
            .animateItemPlacement()
            // .fillParentMaxHeight()
            .aspectRatio(2 / 3f),
        )
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
}

@Composable
private fun ProfileTitle(
  profileViewState: ProfileViewState,
) {
  if (profileViewState.user is State.Success) {
    val user = profileViewState.user.data
    Row(
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
