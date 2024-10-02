/*
 * Copyright 2023, Google LLC, Christopher Banes and the Tivi project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package app.tivi.util

import kotlinx.coroutines.CancellationException

inline fun <T, R> T.cancellableRunCatching(block: T.() -> R): Result<R> = try {
  Result.success(block())
} catch (ce: CancellationException) {
  throw ce
} catch (e: Throwable) {
  Result.failure(e)
}
