/*
 * Copyright (C) 2020 Sandip Vaghela
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

package com.afterroot.watchdone.data

import com.afterroot.tmdbapi.model.MovieDb
import com.afterroot.tmdbapi.model.Multi
import com.afterroot.tmdbapi.model.tv.TvSeries
import com.afterroot.watchdone.data.base.AdditionalParams
import com.afterroot.watchdone.data.base.DataHolder
import com.google.firebase.firestore.QuerySnapshot

data class MultiDataHolder(
    override var data: Multi,
    override var additionalParams: MultiAdditionalParams? = null
) : DataHolder<Multi, MultiAdditionalParams>()

data class MultiAdditionalParams(val type: String) : AdditionalParams

fun QuerySnapshot.toMultiDataHolder(): List<MultiDataHolder> {
    val list = mutableListOf<MultiDataHolder>()
    this.forEach { queryDocumentSnapshot ->
        val type = queryDocumentSnapshot.getString(Field.MEDIA_TYPE)
        val holder: MultiDataHolder
        holder = when (type) {
            Field.MEDIA_TYPE_MOVIE -> {
                MultiDataHolder(
                    queryDocumentSnapshot.toObject(MovieDb::class.java),
                    MultiAdditionalParams(Field.MEDIA_TYPE_MOVIE)
                )
            }
            Field.MEDIA_TYPE_TV -> {
                MultiDataHolder(
                    queryDocumentSnapshot.toObject(TvSeries::class.java),
                    MultiAdditionalParams(Field.MEDIA_TYPE_TV)
                )
            }
            else -> {
                MultiDataHolder(MovieDb(), MultiAdditionalParams(Field.MEDIA_TYPE_MOVIE))
            }
        }
        list.add(holder)
    }
    return list
}