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
