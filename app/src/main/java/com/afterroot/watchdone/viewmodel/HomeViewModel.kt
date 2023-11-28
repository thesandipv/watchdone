/*
 * Copyright (C) 2020-2023 Sandip Vaghela
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
package com.afterroot.watchdone.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.afterroot.watchdone.utils.FirestoreMigrations
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val savedState: SavedStateHandle? = null,
    // private val authRepository: AuthRepository,
    private val firestoreMigrations: FirestoreMigrations,
) : ViewModel() {

    fun getResponseRequestToken() {
    // FIXME ClassCastException
        /*return liveData(Dispatchers.IO) {
            emit( // TODO Deeplink properly
                authRepository.createRequestToken(RequestBodyToken(Deeplink.launch)),
            )
        }*/
    }

    suspend fun checkForMigrations() {
        firestoreMigrations.start()
    }
}
