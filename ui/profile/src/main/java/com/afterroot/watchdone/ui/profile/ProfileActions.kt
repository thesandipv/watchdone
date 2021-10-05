/*
 * 2021 AfterROOT
 */
package com.afterroot.watchdone.ui.profile

import com.afterroot.watchdone.data.model.LocalUser
import com.afterroot.ui.common.view.UiMessage

sealed class ProfileActions {
    data class SaveProfile(val localUser: LocalUser) : ProfileActions()
    data class ShowMessage(val message: UiMessage) : ProfileActions()
    object EditProfile : ProfileActions()
    object Nothing : ProfileActions()
    object Refresh : ProfileActions()
    object SignOut : ProfileActions()
    object Up : ProfileActions()
}
