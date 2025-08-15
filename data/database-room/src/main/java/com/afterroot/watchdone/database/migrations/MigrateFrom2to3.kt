/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.database.migrations

import androidx.room.RenameColumn
import androidx.room.migration.AutoMigrationSpec

@RenameColumn.Entries(
  RenameColumn(
    tableName = "media",
    fromColumnName = "isWatched",
    toColumnName = "is_watched",
  ),
  RenameColumn(
    tableName = "media",
    fromColumnName = "tmdbId",
    toColumnName = "tmdb_id",
  ),
)
class MigrateFrom2to3 : AutoMigrationSpec
