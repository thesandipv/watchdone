/*
 * Copyright (C) 2020-2022 Sandip Vaghela
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
package com.afterroot.watchdone.diff

import androidx.recyclerview.widget.DiffUtil
import com.afterroot.watchdone.data.model.TV

class TVDiffCallback : DiffUtil.ItemCallback<TV>() {
    override fun areItemsTheSame(oldItem: TV, newItem: TV): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TV, newItem: TV): Boolean {
        return oldItem == newItem
    }
}
