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
package com.afterroot.watchdone.helpers

import androidx.core.net.toUri
import com.afterroot.watchdone.base.Constants
import info.movito.themoviedbapi.model.Multi
import okhttp3.HttpUrl

object Deeplink {
    fun media(mediaId: Int, mediaType: Multi.MediaType) = HttpUrl.Builder()
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
}
