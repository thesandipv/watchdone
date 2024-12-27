/*
 * Copyright 2023, Google LLC, Christopher Banes and the Tivi project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package app.tivi.data.daos

import com.afterroot.watchdone.data.compoundmodel.EntryWithMedia
import com.afterroot.watchdone.data.daos.EntryDao
import com.afterroot.watchdone.data.model.Entry

interface RoomEntryDao<EC : Entry, LI : EntryWithMedia<EC>> :
  EntryDao<EC, LI>,
  RoomEntityDao<EC>
