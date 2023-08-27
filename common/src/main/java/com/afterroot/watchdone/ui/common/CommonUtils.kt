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
package com.afterroot.watchdone.ui.common

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.afterroot.utils.network.NetworkState
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * @return [AlertDialog] based on [NetworkState]
 */
fun Context.showNetworkDialog(
    state: NetworkState,
    positive: () -> Unit,
    negative: () -> Unit,
    isShowHide: Boolean = false
): AlertDialog {
    val dialog = MaterialAlertDialogBuilder(this).apply {
        setTitle(
            if (state == NetworkState.CONNECTION_LOST) "Connection Lost" else "Network Disconnected"
        )
        setCancelable(false)
        setMessage(com.afterroot.watchdone.resources.R.string.dialog_msg_no_network)
        setNegativeButton("Exit") { _, _ -> negative() }
        if (isShowHide) {
            setPositiveButton("Hide") { dialog, _ -> dialog.dismiss() }
        } else {
            setPositiveButton("Retry") { _, _ -> positive() }
        }
    }
    return dialog.show()
}
