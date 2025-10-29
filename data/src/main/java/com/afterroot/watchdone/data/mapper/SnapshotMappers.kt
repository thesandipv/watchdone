/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.data.mapper

import com.afterroot.data.model.NetworkUser
import com.afterroot.watchdone.data.model.DBMedia
import com.afterroot.watchdone.data.model.Media
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

fun QuerySnapshot.toMedia(): List<Media> = toObjects(DBMedia::class.java).map {
  it.toMedia()
}

fun DocumentSnapshot.toMedia(): Media = toObject(DBMedia::class.java)?.toMedia() ?: Media.EMPTY

fun QuerySnapshot.toNetworkUser(): NetworkUser =
  this.documents[0].toObject(NetworkUser::class.java) ?: NetworkUser()
