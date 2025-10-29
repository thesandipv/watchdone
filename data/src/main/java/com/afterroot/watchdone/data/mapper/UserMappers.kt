/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.data.mapper

import com.afterroot.data.model.NetworkUser
import com.afterroot.watchdone.data.model.LocalUser

fun LocalUser.toNetworkUser(): NetworkUser = NetworkUser(
  name = name,
  email = email,
  uid = uid,
  fcmId = fcmId,
  userName = userName,
  properties = properties,
)

fun NetworkUser.toLocalUser(): LocalUser = LocalUser(
  name = name,
  email = email,
  uid = uid,
  fcmId = fcmId,
  userName = userName,
  properties = properties,
)
