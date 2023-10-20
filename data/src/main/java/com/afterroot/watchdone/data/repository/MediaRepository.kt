/*
 * Copyright (C) 2020-2023 Sandip Vaghela
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.afterroot.watchdone.data.repository

import com.afterroot.watchdone.data.mapper.MediaEntityToMedia
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.database.dao.MediaDao
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class MediaQuery(val mediaIds: Set<Int>? = null)

interface MediaRepository {
    fun getMedia(query: MediaQuery = MediaQuery(mediaIds = null)): Flow<List<Media>>
}

class MediaRepositoryImpl @Inject constructor(
    private val mediaDao: MediaDao,
    private val mapper: MediaEntityToMedia,
) : MediaRepository {
    override fun getMedia(query: MediaQuery): Flow<List<Media>> {
        return mediaDao.getMedia(
            ids = query.mediaIds ?: emptySet(),
            filterIds = query.mediaIds != null,
        ).map { it.map(mapper::map) }
    }
}
