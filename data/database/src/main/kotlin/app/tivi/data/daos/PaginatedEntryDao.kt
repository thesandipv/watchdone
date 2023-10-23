/*
 * Copyright 2023, Google LLC, Christopher Banes and the Tivi project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package app.tivi.data.daos

import com.afterroot.watchdone.data.compoundmodel.EntryWithMedia
import com.afterroot.watchdone.data.model.PaginatedEntry

interface PaginatedEntryDao<EC : PaginatedEntry, LI : EntryWithMedia<EC>> : EntryDao<EC, LI> {
    suspend fun deletePage(page: Int)
    suspend fun getLastPage(): Int?
}

suspend fun <EC : PaginatedEntry, LI : EntryWithMedia<EC>> PaginatedEntryDao<EC, LI>.updatePage(
    page: Int,
    entities: List<EC>,
) {
    deletePage(page)
    upsertAll(entities)
}
