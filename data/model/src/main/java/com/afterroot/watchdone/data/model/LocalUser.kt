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
package com.afterroot.watchdone.data.model

import com.afterroot.data.model.UserProperties
import com.afterroot.data.utils.valueOrBlank
import java.io.Serializable

// Collection 'users'
data class LocalUser(
    var name: String? = null,
    var email: String? = null,
    var uid: String? = null,
    var fcmId: String? = null,
    var userName: String? = null,
    var isUserNameAvailable: Boolean = userName.valueOrBlank().isNotBlank(),
    var properties: UserProperties = UserProperties(),
) : Serializable {
    fun trim(): LocalUser = copy(name = name?.trim(), userName = userName?.trim())
}