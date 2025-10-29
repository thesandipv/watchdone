/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.tmdb.account

import app.moviebase.tmdb.model.TmdbAccountDetails

interface TmdbAccountActions {
  suspend fun getAccountDetails(): TmdbAccountDetails
}
