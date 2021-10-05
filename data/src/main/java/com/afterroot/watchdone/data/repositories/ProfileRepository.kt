/*
 * 2021 AfterROOT
 */
package com.afterroot.watchdone.data.repositories

import com.afterroot.watchdone.base.CoroutineDispatchers
import com.afterroot.watchdone.base.Field
import com.afterroot.watchdone.data.mapper.toNetworkUser
import com.afterroot.watchdone.data.model.LocalUser
import com.afterroot.watchdone.utils.State
import com.afterroot.watchdone.utils.collectionUsers
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseMessaging: FirebaseMessaging,
    private val dispatchers: CoroutineDispatchers
) {
    fun getProfile(uid: String, cached: Boolean = false) = flow {
        emit(State.loading())

        val source = if (cached) Source.CACHE else Source.DEFAULT

        val query = firestore.collectionUsers().whereEqualTo(Field.UID, uid).get(source)
        val result = query.await()

        emit(State.success(result.toNetworkUser()))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(dispatchers.io)

    fun setProfile(uid: String, localUser: LocalUser) = flow {
        emit(State.loading())

        val userRef = firestore.collectionUsers().document(uid)
        val token = getFCMToken()

        userRef.set(localUser.copy(fcmId = token).toNetworkUser(), SetOptions.merge()).await()

        emit(State.success(true))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(dispatchers.io)

    private suspend fun getFCMToken(): String {
        return firebaseMessaging.token.await()
    }
}
