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

  fun successResult(): T? {
    return if (this is Success) {
      data
    } else {
      null
    }
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
  fun composeWhen(
    failed: @Composable (message: String, exception: Throwable?) -> Unit,
  ): State<T> {
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
