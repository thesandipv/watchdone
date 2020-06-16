/*
 * Copyright (C) 2020 Sandip Vaghela
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
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.afterroot.watchdone.BuildConfig
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

fun dp2px(context: Context, dp: Int): Int {
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val metrics = DisplayMetrics()
    display.getMetrics(metrics)
    return (dp * metrics.density + 0.5f).toInt()
}

fun px2dp(context: Context, px: Int): Int {
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val displaymetrics = DisplayMetrics()
    display.getMetrics(displaymetrics)
    return (px / displaymetrics.density + 0.5f).toInt()
}

fun Context.getScreenWidth(): Int {
    val size = Point()
    (this as Activity).windowManager.defaultDisplay.getSize(size)
    return size.x
}

fun getMailBodyForFeedback(): String {
    val builder = StringBuilder().apply {
        appendln("----Do not remove this info----")
        appendln("Version : ${BuildConfig.VERSION_NAME}")
        appendln("Version Code : ${BuildConfig.VERSION_CODE}")
        appendln("User ID : ${FirebaseUtils.firebaseUser?.uid}")
        appendln("----Do not remove this info----")
    }
    return builder.toString()
}