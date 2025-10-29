/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.helpers

import androidx.core.net.toUri
import com.afterroot.watchdone.base.Constants
import com.afterroot.watchdone.data.model.MediaType
import okhttp3.HttpUrl

object Deeplink {
  fun media(mediaId: Int, mediaType: MediaType) = HttpUrl.Builder()
    .scheme(Constants.SCHEME_HTTPS)
    .host(Constants.WATCHDONE_HOST)
    .addPathSegment("media")
    .addPathSegment(mediaType.name)
    .addPathSegment(mediaId.toString())
    .build().toString().toUri()

  val launch = HttpUrl.Builder()
    .scheme(Constants.SCHEME_HTTPS)
    .host(Constants.AFTERROOT_HOST)
    .addPathSegment("apps")
    .addPathSegment("watchdone")
    .addPathSegment("launch")
    .build().toString()

  val authSuccess = "${Constants.scheme_watchdone}://${Constants.WATCHDONE_HOST}/tmdb/auth/success"
}
