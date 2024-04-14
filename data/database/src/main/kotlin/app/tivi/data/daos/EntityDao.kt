/*
 * Copyright 2023, Google LLC, Christopher Banes and the Tivi project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package app.tivi.data.daos

import com.afterroot.watchdone.data.model.WDEntity

interface EntityDao<in E : WDEntity> {
  suspend fun upsert(entity: E): Long

  suspend fun upsertAll(entities: List<E>)

  suspend fun deleteEntity(entity: E)
}

suspend inline fun <E : WDEntity> EntityDao<E>.upsertAll(vararg entities: E) {
  upsertAll(entities.toList())
}
