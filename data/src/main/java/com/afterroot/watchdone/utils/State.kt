/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.utils

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

typealias FlowOfState<T> = Flow<State<T>>
typealias StateFlowOfState<T> = StateFlow<State<T>>

sealed interface State<out T> {
  data object Loading : State<Nothing>
  data class Success<T>(val data: T) : State<T>
  data class Failed(val message: String, val exception: Throwable? = null) : State<Nothing>

  fun doWhen(
    success: (T) -> Unit,
    loading: () -> Unit = {},
    failed: (message: String) -> Unit = {},
  ) = when (this) {
    is Success -> success(data)
    is Failed -> failed(this.message)
    is Loading -> loading()
  }

  fun <R> returnWhen(
    success: (T) -> R,
    loading: (() -> R)? = null,
    failed: ((message: String) -> R)? = null,
  ) = when (this) {
    is Success -> success(data)
    is Failed -> failed?.invoke(this.message)
    is Loading -> loading?.invoke()
  }

  fun whenSuccess(success: (T) -> Unit): State<T> {
    if (this is Success) success(data)
    return this
  }

  fun successResult(): T? = if (this is Success) {
    data
  } else {
    null
  }

  @Composable
  fun composeWhen(success: @Composable (T) -> Unit): State<T> {
    if (this is Success) success(data)
    return this
  }

  fun whenFailed(failed: (message: String, exception: Throwable?) -> Unit): State<T> {
    if (this is Failed) failed(message, exception)
    return this
  }

  @Composable
  fun composeWhen(failed: @Composable (message: String, exception: Throwable?) -> Unit): State<T> {
    if (this is Failed) failed(message, exception)
    return this
  }

  fun whenLoading(loading: () -> Unit): State<T> {
    if (this is Loading) loading()
    return this
  }

  @Composable
  fun composeWhen(loading: @Composable () -> Unit): State<T> {
    if (this is Loading) loading()
    return this
  }
}

suspend fun <T> FlowCollector<State<T>>.emitSuccess(value: T) {
  emit(State.Success(value))
}

suspend fun <T> FlowCollector<State<T>>.emitFailed(message: String, exception: Throwable? = null) {
  emit(State.Failed(message, exception))
}

fun <T> Flow<T>.asState(): FlowOfState<T> = map<T, State<T>> { State.Success(it) }
  .onStart { emit(State.Loading) }
  .catch { emit(State.Failed(it.message.toString(), it)) }
