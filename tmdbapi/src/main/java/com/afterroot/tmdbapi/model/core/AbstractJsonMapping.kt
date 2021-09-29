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
package com.afterroot.tmdbapi.model.core

import android.util.Log
import com.fasterxml.jackson.annotation.JsonAnySetter
import java.io.Serializable

/**
 * @author Sandip Vaghela
 */
abstract class AbstractJsonMapping : Serializable {
    @JsonAnySetter
    open fun handleUnknown(key: String?, value: Any?) {
        val unknown = "Unknown property: '$key'"
        val result = kotlin.runCatching {
            Log.d(TAG, "handleUnknown: $unknown")
        }
        if (result.isFailure) {
            println("handleUnknown: $unknown")
        }
    }

    companion object {
        private const val TAG = "AbstractJsonMapping"
    }

    // abstract fun type() : Int
}
