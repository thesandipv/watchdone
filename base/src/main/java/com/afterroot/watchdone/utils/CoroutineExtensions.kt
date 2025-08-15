/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.utils

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
fun <T, R> Flow<T?>.flatMapLatestNullable(transform: suspend (value: T) -> Flow<R>): Flow<R?> =
  flatMapLatest {
    if (it != null) transform(it) else flowOf(null)
  }

fun <T, R> Flow<T?>.mapNullable(transform: suspend (value: T) -> R): Flow<R?> = map {
  if (it != null) transform(it) else null
}

fun <T> delayFlow(timeout: Long, value: T): Flow<T> = flow {
  delay(timeout)
  emit(value)
}

fun CoroutineScope.launchOrThrow(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  block: suspend CoroutineScope.() -> Unit,
): Job = launch(context, start, block).also {
  check(!it.isCancelled) {
    "launch failed. Job is already cancelled"
  }
}
