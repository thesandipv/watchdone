/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.ui.profile

import app.tivi.api.UiMessage
import com.afterroot.watchdone.data.model.LocalUser

sealed class ProfileActions {
  data class SaveProfile(val localUser: LocalUser) : ProfileActions()
  data class ShowMessage(val message: UiMessage) : ProfileActions()
  data object EditProfile : ProfileActions()
  data object Nothing : ProfileActions()
  data object Refresh : ProfileActions()
  data object SignOut : ProfileActions()
  data object Up : ProfileActions()
}
