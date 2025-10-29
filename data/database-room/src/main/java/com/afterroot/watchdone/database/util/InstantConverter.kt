/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.database.util

import androidx.room.TypeConverter
import kotlin.time.Instant

class InstantConverter {
  @TypeConverter
  fun longToInstant(value: Long?): Instant? = value?.let(Instant::fromEpochMilliseconds)

  @TypeConverter
  fun instantToLong(instant: Instant?): Long? = instant?.toEpochMilliseconds()
}
