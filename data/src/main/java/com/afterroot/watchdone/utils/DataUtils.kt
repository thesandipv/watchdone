/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.utils

import com.afterroot.data.utils.FirebaseUtils
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

fun getMailBodyForFeedback(
  firebaseUtils: FirebaseUtils,
  version: String,
  versionCode: Int,
): String {
  val builder = StringBuilder().apply {
    appendLine("----Do not remove this info----")
    appendLine("Version : $version")
    appendLine("Version Code : $versionCode")
    appendLine("User ID : ${firebaseUtils.uid}")
    appendLine("----Do not remove this info----")
  }
  return builder.toString()
}

fun <T> resultFlow(value: T, coroutineContext: CoroutineContext = Dispatchers.IO) =
  resultFlow(coroutineContext = coroutineContext) {
    emit(State.success(value))
  }

fun <T> resultFlow(
  result: T,
  coroutineContext: CoroutineContext = Dispatchers.IO,
  executeBeforeResult: suspend () -> Unit,
) = resultFlow(coroutineContext = coroutineContext) {
  executeBeforeResult()
  emit(State.success(result))
}

fun <T> resultFlow(
  coroutineContext: CoroutineContext = Dispatchers.IO,
  block: suspend FlowCollector<State<T>>.() -> Unit,
) = flow {
  emit(State.loading())
  block()
}.catch { exception ->
  emit(State.failed(exception.message.toString(), exception))
}.flowOn(coroutineContext)
