/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Country.TABLE_NAME)
data class Country(
  @PrimaryKey
  val iso: String,
  val englishName: String,
  val nativeName: String,
) {
  companion object {
    const val TABLE_NAME = "countries"
  }
}
