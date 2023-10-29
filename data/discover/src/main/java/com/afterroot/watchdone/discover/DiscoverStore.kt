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

package com.afterroot.watchdone.discover

import app.moviebase.tmdb.model.TmdbDiscover
import app.tivi.data.daos.updatePage
import app.tivi.data.db.DatabaseTransactionRunner
import app.tivi.data.util.storeBuilder
import com.afterroot.watchdone.base.CoroutineDispatchers
import com.afterroot.watchdone.data.daos.DiscoverDao
import com.afterroot.watchdone.data.daos.MediaDao
import com.afterroot.watchdone.data.daos.getIdOrSaveMedia
import com.afterroot.watchdone.data.model.DiscoverEntry
import com.afterroot.watchdone.data.model.MediaType
import javax.inject.Inject
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store

class DiscoverStore @Inject constructor(
    dataSource: DiscoverDataSource,
    discoverDao: DiscoverDao,
    mediaDao: MediaDao,
    discover: TmdbDiscover,
    transactionRunner: DatabaseTransactionRunner,
    dispatchers: CoroutineDispatchers,
) : Store<Int, List<DiscoverEntry>> by storeBuilder(
    fetcher = Fetcher.of { page: Int ->
        dataSource(page, discover.buildParameters()).let { response ->
            withContext(dispatchers.databaseWrite) {
                transactionRunner {
                    response.map { media ->
                        DiscoverEntry(
                            mediaId = mediaDao.getIdOrSaveMedia(media),
                            page = page,
                            mediaType = media.mediaType ?: MediaType.MOVIE,
                        )
                    }
                }
            }
        }
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { page -> discoverDao.entriesForPage(page) },
        writer = { page, response ->
            transactionRunner {
                if (page == 1) {
                    discoverDao.deleteAll()
                    discoverDao.upsertAll(response)
                } else {
                    discoverDao.updatePage(page, response)
                }
            }
        },
        delete = discoverDao::deletePage,
        deleteAll = discoverDao::deleteAll,
    ),
).build()
