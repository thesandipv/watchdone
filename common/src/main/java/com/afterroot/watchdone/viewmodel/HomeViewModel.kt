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
import androidx.lifecycle.liveData
import com.afterroot.tmdbapi.model.RequestBodyToken
import com.afterroot.tmdbapi.repository.AuthRepository
import com.afterroot.watchdone.helpers.Deeplink
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers

@HiltViewModel
class HomeViewModel @Inject constructor(
    val savedState: SavedStateHandle? = null,
    private val authRepository: AuthRepository
) : ViewModel() {

    fun getResponseRequestToken() = liveData(Dispatchers.IO) {
        emit( // TODO Deeplink properly
            authRepository.createRequestToken(RequestBodyToken(Deeplink.launch))
        )
    }
}
