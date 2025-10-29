/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone.domain.interactors

import app.tivi.domain.ResultInteractor
import com.afterroot.data.model.NetworkUser
import com.afterroot.watchdone.data.model.LocalUser
import com.afterroot.watchdone.data.repositories.ProfileRepository
import com.afterroot.watchdone.utils.State
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetProfile @Inject constructor(private val profileRepository: ProfileRepository) :
  ResultInteractor<GetProfile.Params, Flow<State<NetworkUser>>>() {
  data class Params(val uid: String, val cached: Boolean = false)

  override suspend fun doWork(params: Params): Flow<State<NetworkUser>> =
    profileRepository.getProfile(params.uid, params.cached)
}

class SetProfile @Inject constructor(private val profileRepository: ProfileRepository) :
  ResultInteractor<SetProfile.Params, Flow<State<Boolean>>>() {
  data class Params(val uid: String, val localUser: LocalUser)

  override suspend fun doWork(params: Params): Flow<State<Boolean>> =
    profileRepository.setProfile(params.uid, params.localUser)
}

/*
private fun addUserInfoInDB() {
    try {
        val curUser = firebaseUtils.firebaseUser!!
        val userRef = get<FirebaseFirestore>().collection(DatabaseFields.COLLECTION_USERS).document(curUser.uid)
        get<FirebaseMessaging>().token
            .addOnCompleteListener(
                OnCompleteListener { tokenTask ->
                    if (!tokenTask.isSuccessful) {
                        return@OnCompleteListener
                    }
                    userRef.get().addOnCompleteListener { getUserTask ->
                        if (getUserTask.isSuccessful) {
                            if (!getUserTask.result!!.exists()) {
                                sharedViewModel.displayMsg("LocalUser not available. Creating LocalUser..")
                                val localUser = LocalUser(curUser.displayName, curUser.email, curUser.uid, tokenTask.result)
                                userRef.set(localUser).addOnCompleteListener { setUserTask ->
                                    if (!setUserTask.isSuccessful) Log.e(
                                        TAG,
                                        "Can't create firebaseUser",
                                        setUserTask.exception
                                    )
                                }
                            } else if (getUserTask.result!![DatabaseFields.FIELD_FCM_ID] != tokenTask.result) {
                                userRef.update(DatabaseFields.FIELD_FCM_ID, tokenTask.result)
                            }
                        } else Log.e(TAG, "Unknown Error", getUserTask.exception)
                    }
                }
            )
    } catch (e: Exception) {
        Log.e(TAG, "addUserInfoInDB: $e")
    }
}
*/
