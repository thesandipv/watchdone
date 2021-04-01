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
package com.afterroot.watchdone.data.mapper

import com.afterroot.tmdbapi.model.Multi
import com.afterroot.tmdbapi.model.NetworkMovie
import com.afterroot.tmdbapi.model.tv.TvSeries
import com.afterroot.watchdone.base.Field
import com.afterroot.watchdone.data.model.DbMovie
import com.afterroot.watchdone.data.model.Movie
import com.google.firebase.firestore.QuerySnapshot

fun QuerySnapshot.toMulti(): List<Multi> {
    val list = mutableListOf<Multi>()
    this.forEach { queryDocumentSnapshot ->
        val multi: Multi = when (queryDocumentSnapshot.getString(Field.MEDIA_TYPE)) {
            Field.MEDIA_TYPE_MOVIE -> {
                queryDocumentSnapshot.toObject(DbMovie::class.java).toMovie()
            }
            Field.MEDIA_TYPE_TV -> {
                queryDocumentSnapshot.toObject(TvSeries::class.java)
                    .toTV(isWatched = queryDocumentSnapshot.getBoolean(Field.IS_WATCHED) ?: false)
            }
            else -> {
                Movie()
            }
        }
        list.add(multi)
    }
    return list
}

fun QuerySnapshot.toMovies(): List<Movie> {
    val list = mutableListOf<Movie>()
    this.forEach { queryDocumentSnapshot ->
        list.add(
            queryDocumentSnapshot.toObject(NetworkMovie::class.java)
                .toMovie(isWatched = queryDocumentSnapshot.getBoolean(Field.IS_WATCHED) ?: false)
        )
    }
    return list
}
