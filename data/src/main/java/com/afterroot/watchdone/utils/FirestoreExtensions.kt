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
package com.afterroot.watchdone.utils

import com.afterroot.watchdone.base.Collection
import com.afterroot.watchdone.data.model.LocalUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Extension function for getting [CollectionReference] of Watchdone Database root
 * @return [CollectionReference] of Watchdone Database Root
 * @param id Id of User
 * @param isUseOnlyProdDB true will always return [CollectionReference] of Production Database
 * otherwise it will based on BuildConfig.DEBUG
 */
fun FirebaseFirestore.collectionWatchdone(id: String, isUseOnlyProdDB: Boolean): CollectionReference {
    return collection(Collection.USERS).document(id)
        .collection(if (isUseOnlyProdDB) Collection.WATCHDONE_PROD else Collection.WATCHDONE_AUTO)
}

fun CollectionReference.documentWatchlist(): DocumentReference = document(Collection.WATCHLIST)
fun DocumentReference.collectionWatchlistItems(): CollectionReference = collection(Collection.ITEMS)
fun CollectionReference.collectionWatchlistItems(): CollectionReference = documentWatchlist().collectionWatchlistItems()

fun FirebaseFirestore.collectionUsers() = collection(Collection.USERS)

fun FirebaseAuth.getLocalUser() = LocalUser(
    name = currentUser?.displayName,
    email = currentUser?.email,
    uid = currentUser?.uid
)
