package com.afterroot.watchdone.discover

import com.afterroot.watchdone.data.model.Media

fun interface DiscoverDataSource {
    suspend operator fun invoke(page: Int, parameters: Map<String, Any?>): List<Media>
}
