package com.afterroot.watchdone.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.afterroot.watchdone.data.daos.MediaDao
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.model.MediaType
import com.afterroot.watchdone.database.WatchdoneDatabase
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class MediaDaoTest {
    private lateinit var mediaDao: MediaDao
    private lateinit var database: WatchdoneDatabase

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, WatchdoneDatabase::class.java).build()
        mediaDao = database.mediaDao()
    }

    @Test
    fun insert_and_read_mediaEntity() = runTest {
        val mediaEntities = listOf(
            testMediaEntity(1),
            testMediaEntity(2),
            testMediaEntity(3),
            testMediaEntity(4),
        )

        mediaDao.upsertAll(mediaEntities)

        val savedMediaEntities = mediaDao.getMediaByIds(listOf(1, 2, 4, 3)).first()

        assertEquals(
            listOf<Long>(1, 2, 3, 4),
            savedMediaEntities.map {
                it.id
            },
        )
    }

    private fun testMediaEntity(id: Long) = Media(
        id = id,
        releaseDate = "",
        title = "",
        isWatched = false,
        posterPath = "",
        mediaType = MediaType.MOVIE,
        rating = 0.1f,
        tmdbId = 1,
    )
}
