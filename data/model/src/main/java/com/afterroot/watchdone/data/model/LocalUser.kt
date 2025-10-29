/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.data.model

import androidx.compose.runtime.Immutable
import com.afterroot.data.model.UserProperties
import java.io.Serializable

/**
 * Local Model of [com.afterroot.data.model.NetworkUser]
 */
@Immutable
data class LocalUser(
  val name: String? = null,
  val email: String? = null,
  val uid: String? = null,
  val fcmId: String? = null,
  val userName: String? = null,
  val isUserNameAvailable: Boolean = userName.orEmpty().isNotBlank(),
  val properties: UserProperties = UserProperties(),
) : Serializable {
  fun trim(): LocalUser = copy(name = name?.trim(), userName = userName?.trim())
}
