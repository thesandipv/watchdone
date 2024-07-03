package com.afterroot.watchdone.discover

import app.moviebase.tmdb.discover.DiscoverCategory
import com.afterroot.watchdone.data.model.Media
import com.afterroot.watchdone.data.model.MediaType

fun interface DiscoverDataSource {
  @Deprecated("Use another overload for feting discover by category", ReplaceWith(""))
  suspend operator fun invoke(page: Int, parameters: Map<String, Any?>): List<Media>
  suspend operator fun invoke(
    page: Int,
    mediaType: MediaType,
    category: DiscoverCategory,
  ): List<Media> = TODO("Not Implemented")
}
