/*
 * 2021 AfterROOT
 */
package com.afterroot.watchdone.domain.interactors

import com.afterroot.data.model.NetworkUser
import com.afterroot.watchdone.data.model.LocalUser
import com.afterroot.watchdone.data.repositories.ProfileRepository
import com.afterroot.watchdone.domain.ResultInteractor
import com.afterroot.watchdone.utils.State
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfile @Inject constructor(private val profileRepository: ProfileRepository) :
    ResultInteractor<GetProfile.Params, Flow<State<NetworkUser>>>() {
    data class Params(val uid: String, val cached: Boolean = false)

    override suspend fun doWork(params: Params): Flow<State<NetworkUser>> {
        return profileRepository.getProfile(params.uid)
    }
}

class SetProfile @Inject constructor(private val profileRepository: ProfileRepository) :
    ResultInteractor<SetProfile.Params, Flow<State<Boolean>>>() {
    data class Params(val uid: String, val localUser: LocalUser)

    override suspend fun doWork(params: Params): Flow<State<Boolean>> {
        return profileRepository.setProfile(params.uid, params.localUser)
    }
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
