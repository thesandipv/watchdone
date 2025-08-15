/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.tmdb.account

import app.moviebase.tmdb.Tmdb3
import app.moviebase.tmdb.model.TmdbAccountDetails
import javax.inject.Inject

class TmdbAccountActionsImpl @Inject constructor(val tmdb3: Tmdb3) : TmdbAccountActions {
  override suspend fun getAccountDetails(): TmdbAccountDetails = tmdb3.account.getDetails()
}
