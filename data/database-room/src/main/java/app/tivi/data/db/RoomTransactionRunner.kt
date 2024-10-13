/*
 * Copyright 2023, Google LLC, Christopher Banes and the Tivi project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package app.tivi.data.db

import androidx.room.withTransaction
import com.afterroot.watchdone.database.WatchdoneDatabase
import javax.inject.Inject

class RoomTransactionRunner @Inject constructor(private val db: WatchdoneDatabase) :
  DatabaseTransactionRunner {
  override suspend operator fun <T> invoke(block: suspend () -> T): T = db.withTransaction {
    block()
  }
}
