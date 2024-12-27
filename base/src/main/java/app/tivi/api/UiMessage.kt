/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.tivi.api

import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class UiMessage(val message: String, val id: Long = UUID.randomUUID().mostSignificantBits)

fun UiMessage(t: Throwable, id: Long = UUID.randomUUID().mostSignificantBits): UiMessage =
  UiMessage(
    message = t.message ?: "Error occurred: $t",
    id = id,
  )

class UiMessageManager {
  private val mutex = Mutex()

  private val _message = MutableStateFlow(emptyList<UiMessage>())

  /**
   * A flow emitting the current message to display.
   */
  val message: Flow<UiMessage?> = _message.map { it.firstOrNull() }.distinctUntilChanged()

  suspend fun emitMessage(message: UiMessage) {
    mutex.withLock {
      _message.value += message
    }
  }

  suspend fun clearMessage(id: Long) {
    mutex.withLock {
      _message.value = _message.value.filterNot { it.id == id }
    }
  }
}
