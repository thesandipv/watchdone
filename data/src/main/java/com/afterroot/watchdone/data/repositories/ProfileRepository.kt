/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.data.repositories

import com.afterroot.watchdone.base.CoroutineDispatchers
import com.afterroot.watchdone.base.Field
import com.afterroot.watchdone.data.mapper.toNetworkUser
import com.afterroot.watchdone.data.model.LocalUser
import com.afterroot.watchdone.utils.State
import com.afterroot.watchdone.utils.collectionUsers
import com.afterroot.watchdone.utils.resultFlow
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class ProfileRepository @Inject constructor(
  private val firestore: FirebaseFirestore,
  private val firebaseMessaging: FirebaseMessaging,
  private val dispatchers: CoroutineDispatchers,
) {
  fun getProfile(uid: String, cached: Boolean = false) =
    resultFlow(coroutineContext = dispatchers.io) {
      val source = if (cached) Source.CACHE else Source.DEFAULT
      val query = firestore.collectionUsers().whereEqualTo(Field.UID, uid).get(source)
      emit(State.success(query.await().toNetworkUser()))
    }

  fun setProfile(uid: String, localUser: LocalUser) = resultFlow(
    coroutineContext = dispatchers.io,
  ) {
    val userRef = firestore.collectionUsers().document(uid)
    val token = getFCMToken()
    userRef.set(localUser.copy(fcmId = token).toNetworkUser(), SetOptions.merge()).await()
    emit(State.success(true))
  }

  private suspend fun getFCMToken(): String = firebaseMessaging.token.await()
}
