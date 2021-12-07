/*
 * Copyright (C) 2020-2021 Sandip Vaghela
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
package com.afterroot.watchdone.utils

import android.util.Log

const val FORCE_LOGGING = false
const val PREFIX = "WATCHDONE"

fun logD(tag: String, msg: String) {
    if (FORCE_LOGGING) {
        logDebug(tag, msg)
    } else {
        whenBuildIs(debug = { logDebug(tag, msg) })
    }
}

fun logE(tag: String, msg: String) {
    if (FORCE_LOGGING) {
        logError(tag, msg)
    } else {
        whenBuildIs(debug = { logError(tag, msg) })
    }
}

internal fun logDebug(tag: String, msg: String) = Log.d("$PREFIX/$tag", msg)
internal fun logError(tag: String, msg: String) = Log.e("$PREFIX/$tag", msg)
