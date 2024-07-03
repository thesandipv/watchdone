package com.afterroot.watchdone.discover

import app.moviebase.tmdb.discover.DiscoverCategory
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.model.MediaType

fun interface DiscoverDataSource {
  suspend operator fun invoke(
    page: Int,
    mediaType: MediaType,
    category: DiscoverCategory,
  ): List<Media>
}
