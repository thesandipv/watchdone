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
package com.afterroot.watchdone.data.mapper

import com.afterroot.data.model.NetworkUser
import com.afterroot.watchdone.base.Field
import com.afterroot.watchdone.data.model.DBMedia
import com.afterroot.watchdone.data.model.Movie
import com.afterroot.watchdone.data.model.TV
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import info.movito.themoviedbapi.model.Multi
import info.movito.themoviedbapi.model.NetworkMovie

fun QuerySnapshot.toMulti(): List<Multi> {
    val list = mutableListOf<Multi>()
    this.forEach { queryDocumentSnapshot ->
        val multi: Multi = when (queryDocumentSnapshot.getString(Field.MEDIA_TYPE)) {
            Multi.MediaType.MOVIE.name -> {
                queryDocumentSnapshot.toObject(DBMedia::class.java).toMovie()
            }
            Multi.MediaType.TV_SERIES.name -> {
                queryDocumentSnapshot.toObject(DBMedia::class.java).toTV()
            }
            else -> {
                Movie()
            }
        }
        list.add(multi)
    }
    return list
}

fun DocumentSnapshot.toMulti(): Multi {
    return when (getString(Field.MEDIA_TYPE)) {
        Multi.MediaType.MOVIE.name -> {
            toObject(DBMedia::class.java)?.toMovie() ?: Movie()
        }
        Multi.MediaType.TV_SERIES.name -> {
            toObject(DBMedia::class.java)?.toTV() ?: TV()
        }
        else -> {
            Movie()
        }
    }
}

fun QuerySnapshot.toMovies(): List<Movie> {
    val list = mutableListOf<Movie>()
    this.forEach { queryDocumentSnapshot ->
        list.add(
            queryDocumentSnapshot.toObject(NetworkMovie::class.java)
                .toMovie(isWatched = queryDocumentSnapshot.getBoolean(Field.IS_WATCHED) ?: false),
        )
    }
    return list
}

fun QuerySnapshot.toNetworkUser(): NetworkUser =
    this.documents[0].toObject(NetworkUser::class.java) ?: NetworkUser()
