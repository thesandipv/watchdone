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
package com.afterroot.ui.common.view

import com.afterroot.watchdone.utils.delayFlow
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

/**
 * Copied from Tivi
 */
class SnackbarManager @Inject constructor() {
    // We want a maximum of 3 messages queued
    private val pendingMessages = Channel<UiMessage>(3, BufferOverflow.DROP_OLDEST)
    private val removeMessageSignal = Channel<Unit>(Channel.RENDEZVOUS)

    /**
     * A flow of [UiMessage]s to display in the UI, usually as snackbars. The flow will immediately
     * emit `null`, and will then emit messages sent via [addMessage]. Once 6 seconds has elapsed,
     * or [removeCurrentMessage] is called (if before that) `null` will be emitted to remove
     * the current message.
     */
    val messages: Flow<UiMessage?> = flow {
        emit(null)

        pendingMessages.receiveAsFlow().collect {
            emit(it)

            // Wait for either a 6 second timeout, or a remove signal (whichever comes first)
            merge(
                delayFlow(6000, Unit),
                removeMessageSignal.receiveAsFlow()
            ).firstOrNull()

            // Remove the message
            emit(null)
        }
    }

    /**
     * Add [message] to the queue of messages to display.
     */
    suspend fun addMessage(message: UiMessage) {
        pendingMessages.send(message)
    }

    /**
     * Remove the current message from being displayed.
     */
    suspend fun removeCurrentMessage() {
        removeMessageSignal.send(Unit)
    }
}
