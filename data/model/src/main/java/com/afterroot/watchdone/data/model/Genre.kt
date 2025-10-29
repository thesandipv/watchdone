/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Genre.TABLE_NAME)
data class Genre(
  @PrimaryKey
  val id: Int,
  val name: String,
) {
  companion object {
    const val TABLE_NAME = "genres"
  }
}
