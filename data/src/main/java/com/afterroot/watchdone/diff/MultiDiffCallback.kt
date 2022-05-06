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
import com.afterroot.tmdbapi.model.Multi
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV

class MultiDiffCallback : DiffUtil.ItemCallback<Multi>() {
    override fun areItemsTheSame(oldItem: Multi, newItem: Multi): Boolean {
        return if (oldItem is Movie && newItem is Movie) {
            oldItem.id == newItem.id
        } else if (oldItem is TV && newItem is TV) {
            oldItem.id == newItem.id
        } else if (oldItem is Movie && newItem is TV) {
            oldItem.id == newItem.id
        } else if (oldItem is TV && newItem is Movie) {
            oldItem.id == newItem.id
        } else false
    }

    @Suppress("ReplaceCallWithBinaryOperator")
    override fun areContentsTheSame(oldItem: Multi, newItem: Multi): Boolean {
        return if (oldItem is Movie && newItem is Movie) {
            oldItem.equals(newItem)
        } else if (oldItem is TV && newItem is TV) {
            oldItem.equals(newItem)
        } else if (oldItem is Movie && newItem is TV) {
            false
        } else if (oldItem is TV && newItem is Movie) {
            false
        } else false
    }
}
