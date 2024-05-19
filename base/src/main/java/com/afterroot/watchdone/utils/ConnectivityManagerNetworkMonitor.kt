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

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.getSystemService
import com.afterroot.watchdone.base.CoroutineDispatchers
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn

/**
 * Modified from nowinandroid's [ConnectivityManagerNetworkMonitor.kt](https://github.com/android/nowinandroid/blob/c396352d83f9bbf1d86dae3c52c89fb66a9bcd05/core/data/src/main/kotlin/com/google/samples/apps/nowinandroid/core/data/util/ConnectivityManagerNetworkMonitor.kt)
 */
class ConnectivityManagerNetworkMonitor @Inject constructor(
  @ApplicationContext private val context: Context,
  dispatchers: CoroutineDispatchers,
) : NetworkMonitor {
  override val isOnline: Flow<Boolean> = callbackFlow {
    val connectivityManager = context.getSystemService<ConnectivityManager>()
    if (connectivityManager == null) {
      channel.trySend(false)
      channel.close()
      return@callbackFlow
    }

    /**
     * The callback's methods are invoked on changes to *any* network matching the [NetworkRequest],
     * not just the active network. So we can simply track the presence (or absence) of such [Network].
     */
    val callback = object : ConnectivityManager.NetworkCallback() {

      private val networks = mutableSetOf<Network>()

      override fun onAvailable(network: Network) {
        networks += network
        channel.trySend(true)
      }

      override fun onLost(network: Network) {
        networks -= network
        channel.trySend(networks.isNotEmpty())
      }
    }

    val request = NetworkRequest.Builder()
      .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
      .build()
    connectivityManager.registerNetworkCallback(request, callback)

    /**
     * Sends the latest connectivity status to the underlying channel.
     */
    channel.trySend(connectivityManager.isCurrentlyConnected())

    awaitClose {
      connectivityManager.unregisterNetworkCallback(callback)
    }
  }
    .flowOn(dispatchers.io)
    .conflate()

  private fun ConnectivityManager.isCurrentlyConnected() = activeNetwork
    ?.let(::getNetworkCapabilities)
    ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
}
