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
package com.afterroot.watchdone.base

import com.afterroot.watchdone.utils.logD
import com.afterroot.watchdone.utils.logE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

sealed class InvokeStatus
object InvokeStarted : InvokeStatus()
object InvokeSuccess : InvokeStatus()
data class InvokeError(val throwable: Throwable) : InvokeStatus()

/**
 * Extension function for Watching the [InvokeStatus]
 * @param scope [CoroutineScope] to launch coroutine in.
 * @param onSuccess Lambda to Execute when [InvokeStatus] is [InvokeSuccess]
 * @return Launched [Job] on specified [scope]
 */
fun Flow<InvokeStatus>.watchStatus(scope: CoroutineScope, tag: String = "", onSuccess: () -> Unit = {}): Job {
    return scope.launch { collectStatus(tag, onSuccess) }
}

/**
 * Collects [InvokeStatus]
 * @param tag Additional log tag prefix
 * @param onSuccess Lambda to Execute when [InvokeStatus] is [InvokeSuccess]
 */
private suspend fun Flow<InvokeStatus>.collectStatus(tag: String = "", onSuccess: () -> Unit = {}) = collect { status ->
    val logTag = if (tag != "") "$tag/" else tag

    when (status) {
        InvokeStarted -> {
            logD("${logTag}InvokeStatus/collectStatus", "Start")
        }
        InvokeSuccess -> {
            logD("${logTag}InvokeStatus/collectStatus", "Success")
            onSuccess()
        }
        is InvokeError -> {
            logE("${logTag}InvokeStatus/collectStatus", "Error: ${status.throwable}")
        }
    }
}
