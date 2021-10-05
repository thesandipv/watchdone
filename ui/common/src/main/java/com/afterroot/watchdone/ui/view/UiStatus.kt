/*
 * 2019 AfterROOT
 */
package com.afterroot.ui.common.view

sealed class UiStatus

object UiIdle : UiStatus()

data class UiMessage(val message: String) : UiStatus()

fun UiMessage(t: Throwable): UiMessage = UiMessage(t.message ?: "Error occurred: $t")

data class UiLoading(val fullRefresh: Boolean = true) : UiStatus()

object UiSuccess : UiStatus()
