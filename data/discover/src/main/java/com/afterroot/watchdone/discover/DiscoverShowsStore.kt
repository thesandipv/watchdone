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
import app.tivi.data.db.DatabaseTransactionRunner
import app.tivi.data.util.storeBuilder
import app.tivi.util.Logger
import com.afterroot.watchdone.base.CoroutineDispatchers
import com.afterroot.watchdone.data.daos.DiscoverDao
import com.afterroot.watchdone.data.daos.MediaDao
import com.afterroot.watchdone.data.daos.getIdOrSaveMedia
import com.afterroot.watchdone.data.daos.updatePage
import com.afterroot.watchdone.data.model.DiscoverEntry
import com.afterroot.watchdone.data.model.MediaType
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store

class DiscoverShowsStore @Inject constructor(
    @Named("tmdbDiscoverShowDataSource") dataSource: DiscoverDataSource,
    discoverDao: DiscoverDao,
    mediaDao: MediaDao,
    discover: TmdbDiscover.Show,
    transactionRunner: DatabaseTransactionRunner,
    dispatchers: CoroutineDispatchers,
    logger: Logger,
) : Store<Int, List<DiscoverEntry>> by storeBuilder(
    fetcher = Fetcher.of { page: Int ->
        dataSource(page, discover.buildParameters()).let { response ->
            withContext(dispatchers.databaseWrite) {
                transactionRunner {
                    response.map { media ->
                        DiscoverEntry(
                            mediaId = mediaDao.getIdOrSaveMedia(media),
                            page = page,
                            mediaType = media.mediaType ?: MediaType.SHOW,
                        )
                    }
                }
            }
        }
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { page ->
            logger.d { "Reading from database:discover page:$page" }
            discoverDao.entriesForPage(page, MediaType.SHOW)
        },
        writer = { page, response ->
            transactionRunner {
                logger.d { "Writing in database:discover page:$page" }
                discoverDao.updatePage(page, response, MediaType.SHOW)
            }
        },
        delete = { discoverDao.deletePage(it, MediaType.SHOW) },
        deleteAll = discoverDao::deleteAll,
    ),
).build()
