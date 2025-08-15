/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.PermissionChecker

class PermissionChecker(private val mContext: Context) {

  fun lacksPermissions(permissions: Array<String>): Boolean = permissions.any {
    lacksPermission(it) == PackageManager.PERMISSION_DENIED
  }

  private fun lacksPermission(permission: String): Int =
    PermissionChecker.checkSelfPermission(mContext, permission)
}
