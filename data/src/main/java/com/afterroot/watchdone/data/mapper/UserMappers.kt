/*
 * 2021 AfterROOT
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
    properties = properties
)

fun NetworkUser.toLocalUser(): LocalUser = LocalUser(
    name = name,
    email = email,
    uid = uid,
    fcmId = fcmId,
    userName = userName,
    properties = properties
)
