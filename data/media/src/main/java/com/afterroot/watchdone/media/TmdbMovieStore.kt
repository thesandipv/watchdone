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

package com.afterroot.watchdone.media

import com.afterroot.watchdone.base.CoroutineDispatchers
import com.afterroot.watchdone.data.mapper.MediaEntityToMedia
import com.afterroot.watchdone.data.mapper.MediaToMediaEntity
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.database.dao.MediaDao
import com.afterroot.watchdone.database.model.MediaEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.flow
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.impl.storeBuilderFromFetcherAndSourceOfTruth

class TmdbMovieStore @Inject constructor(
    dataSource: TmdbMovieDataSource,
    dispatchers: CoroutineDispatchers,
    mediaSOT: MediaSOT,
    mapper: MediaToMediaEntity,
) : Store<Int, Media> by storeBuilderFromFetcherAndSourceOfTruth(
    fetcher = Fetcher.of { tmdbId: Int ->
        dataSource.getMovie(Media(tmdbId = tmdbId)).let(mapper::map)
    },
    sourceOfTruth = mediaSOT(),
).build()

@Singleton
class MediaSOT @Inject constructor(
    private val mediaDao: MediaDao,
    private val mapper: MediaEntityToMedia,
) {
    operator fun invoke(): SourceOfTruth<Int, MediaEntity, Media> = SourceOfTruth.of(
        reader = {
            flow {
                emit(mapper.map(mediaDao.getMedia(it)))
            }
        },
        writer = { _, media ->
            mediaDao.insert(media)
        },
    )
}
