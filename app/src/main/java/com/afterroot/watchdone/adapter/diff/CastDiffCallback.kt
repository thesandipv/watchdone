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

package com.afterroot.watchdone.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.afterroot.watchdone.data.cast.CastDataHolder

class CastDiffCallback : DiffUtil.ItemCallback<CastDataHolder>() {
    override fun areItemsTheSame(oldItem: CastDataHolder, newItem: CastDataHolder): Boolean {
        return oldItem.data.id == newItem.data.id
    }

    override fun areContentsTheSame(oldItem: CastDataHolder, newItem: CastDataHolder): Boolean {
        return oldItem.data == newItem.data
    }
}