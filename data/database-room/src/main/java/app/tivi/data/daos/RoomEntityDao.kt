/*
 * Copyright 2023, Google LLC, Christopher Banes and the Tivi project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package app.tivi.data.daos

import androidx.room.Delete
import androidx.room.Upsert
import com.afterroot.watchdone.data.model.WDEntity

interface RoomEntityDao<in E : WDEntity> : EntityDao<E> {
  @Upsert
  override suspend fun upsert(entity: E): Long

  @Upsert
  override suspend fun upsertAll(entities: List<E>)

  @Delete
  override suspend fun deleteEntity(entity: E)
}
