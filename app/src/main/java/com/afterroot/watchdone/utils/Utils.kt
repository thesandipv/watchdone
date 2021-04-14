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

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowMetrics
import android.view.inputmethod.InputMethodManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afterroot.core.network.NetworkState
import com.afterroot.watchdone.BuildConfig
import com.afterroot.watchdone.R
import com.afterroot.watchdone.settings.Settings
import org.apache.commons.codec.digest.DigestUtils
import java.util.Locale

fun getGravatarUrl(email: String) =
    "https://www.gravatar.com/avatar/${DigestUtils.md5Hex(email.toLowerCase(Locale.getDefault()))}"

fun Context.showKeyboard(view: View) {
    if (view.requestFocus()) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.dp2px(dp: Int): Int {
    return (dp * resources.displayMetrics.density + 0.5f).toInt()
}

fun Context.px2dp(px: Int): Int {
    return (px / resources.displayMetrics.density + 0.5f).toInt()
}

@Suppress("DEPRECATION")
fun Context.getScreenWidth(): Int {
    this as Activity
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val metrics: WindowMetrics = windowManager.currentWindowMetrics
        val bounds: Rect = metrics.bounds
        bounds.width()
    } else {
        val size = Point()
        windowManager.defaultDisplay.getSize(size)
        size.x
    }
}

fun getMailBodyForFeedback(firebaseUtils: FirebaseUtils): String {
    val builder = StringBuilder().apply {
        appendLine("----Do not remove this info----")
        appendLine("Version : ${BuildConfig.VERSION_NAME}")
        appendLine("Version Code : ${BuildConfig.VERSION_CODE}")
        appendLine("User ID : ${firebaseUtils.uid}")
        appendLine("----Do not remove this info----")
    }
    return builder.toString()
}

fun withDelay(millis: Long, block: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed(block, millis)
}

fun Context.showNetworkDialog(state: NetworkState, positive: () -> Unit, negative: () -> Unit) =
    MaterialDialog(this).show {
        title(text = if (state == NetworkState.CONNECTION_LOST) "Connection Lost" else "Network Disconnected")
        cancelable(false)
        message(R.string.dialog_msg_no_network)
        negativeButton(text = "Exit") {
            negative()
        }
        positiveButton(text = "Retry") {
            positive()
        }
    }

/**
 * Helper Function for getting different values for Debug and Release builds
 * @param T type of value to return
 * @param debug value to return if build is Debug
 * @param release value to return if build is Release
 * @since v0.0.4
 * @return either [debug] or [release] with provided type [T]
 */
fun <T> ifDebug(debug: T, release: T): T = if (BuildConfig.DEBUG) debug else release

/**
 * Helper Function for invoking different functions for Debug and Release builds
 * @param T type of value to return
 * @param debug function to invoke if build is Debug
 * @param release function to invoke if build is Release
 * @since v0.0.4
 * @return either [debug] or [release] with provided type [T]
 */
fun <T> ifDebug(debug: () -> T, release: () -> T): T = ifDebug(debug.invoke(), release.invoke())

/**
 * Helper Function for invoking function only if build is Debug
 * @param debug function to invoke if build is Debug
 * @since v0.0.4
 */
fun ifDebug(debug: () -> Unit) {
    if (BuildConfig.DEBUG) debug.invoke()
}

fun Settings.createPosterUrl(path: String) = baseUrl + imageSize + path
