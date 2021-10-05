/*
 * 2021 AfterROOT
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
