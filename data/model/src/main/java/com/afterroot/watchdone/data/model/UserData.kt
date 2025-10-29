/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.model

data class UserData(
  val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
  val useDynamicColor: Boolean = true,
  val isUserSignedIn: Boolean = false,
  val isFirstInstalled: Boolean = true,
  val tmdbBaseUrl: String = "",
  val tmdbPosterSizes: MutableSet<String>? = null,
  val mediaTypeViews: MutableMap<String, String>? = null,
)
