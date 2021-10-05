/*
 * 2021 AfterROOT
 */
package com.afterroot.watchdone.ui.profile

import androidx.compose.runtime.Immutable
import com.afterroot.ui.common.view.UiMessage

@Immutable
data class ProfileViewState(
    val message: UiMessage? = null
) {
    companion object {
        val Empty = ProfileViewState()
    }
}
