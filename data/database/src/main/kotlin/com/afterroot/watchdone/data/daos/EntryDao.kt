/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.daos

import app.tivi.data.daos.EntityDao
import com.afterroot.watchdone.data.compoundmodel.EntryWithMedia
import com.afterroot.watchdone.data.model.Entry
import com.afterroot.watchdone.data.model.MediaType

interface EntryDao<EC : Entry, LI : EntryWithMedia<EC>> : EntityDao<EC> {
  suspend fun deleteAll()
  suspend fun deleteAll(mediaType: MediaType)
}
