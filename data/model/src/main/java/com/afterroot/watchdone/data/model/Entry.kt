/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */

package com.afterroot.watchdone.data.model

interface Entry : WDEntity {
  val mediaId: Long
}

interface PaginatedEntry : Entry {
  val page: Int
}

interface MediaPaginatedEntry : PaginatedEntry {
  val mediaType: MediaType
}
