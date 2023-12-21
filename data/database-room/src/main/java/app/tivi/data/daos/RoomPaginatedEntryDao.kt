/*
 * Copyright 2023, Google LLC, Christopher Banes and the Tivi project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package app.tivi.data.daos

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.afterroot.watchdone.data.compoundmodel.EntryWithMedia
import com.afterroot.watchdone.data.daos.PaginatedEntryDao
import com.afterroot.watchdone.data.model.PaginatedEntry

interface RoomPaginatedEntryDao<EC : PaginatedEntry, LI : EntryWithMedia<EC>> :
    RoomEntryDao<EC, LI>,
    PaginatedEntryDao<EC, LI> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun upsert(entity: EC): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun upsertAll(entities: List<EC>)
}
