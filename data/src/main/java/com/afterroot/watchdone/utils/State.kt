/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.utils

import androidx.compose.runtime.Composable

sealed class State<T> {
  class Loading<T> : State<T>()
  data class Success<T>(val data: T) : State<T>()
  data class Failed<T>(val message: String, val exception: Throwable? = null) : State<T>()

  companion object {
    fun <T> loading() = Loading<T>()
    fun <T> success(data: T) = Success(data)
    fun <T> failed(message: String, exception: Throwable? = null) = Failed<T>(message)
  }

  fun doWhen(
    success: (T) -> Unit,
    loading: () -> Unit = {
    },
    failed: (message: String) -> Unit = {},
  ) = when (this) {
    is Success -> success(data)
    is Failed -> failed(this.message)
    is Loading -> loading()
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
