/*
 * Copyright (C) 2020-2024 Sandip Vaghela
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
