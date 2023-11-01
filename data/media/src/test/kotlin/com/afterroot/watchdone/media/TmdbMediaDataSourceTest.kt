package com.afterroot.watchdone.media

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.afterroot.watchdone.core.testing.AppTest
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.model.MediaType
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TmdbMediaDataSourceTest : AppTest() {
    @Inject lateinit var tmdbMediaDataSource: TmdbMediaDataSource

    @Test
    fun test_Movie_Response() = runTest {
        val media = tmdbMediaDataSource.getMedia(
            Media(tmdbId = 550, mediaType = MediaType.MOVIE),
        )
        assertEquals(
            expected = "Fight Club",
            actual = media.title,
        )
    }

    @Test
    fun test_Series_Response() = runTest {
        val media = tmdbMediaDataSource.getMedia(
            Media(tmdbId = 1399, mediaType = MediaType.SHOW),
        )
        assertEquals(
            expected = "Game of Thrones",
            actual = media.title,
        )
    }
}
