/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
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
fun FirebaseFirestore.collectionWatchdone(
  id: String,
  isUseOnlyProdDB: Boolean,
): CollectionReference = collection(Collection.USERS).document(id)
  .collection(if (isUseOnlyProdDB) Collection.WATCHDONE_PROD else Collection.WATCHDONE_AUTO)

fun CollectionReference.documentWatchlist(): DocumentReference = document(Collection.WATCHLIST)
fun DocumentReference.collectionWatchlistItems(): CollectionReference = collection(Collection.ITEMS)
fun CollectionReference.collectionWatchlistItems(): CollectionReference =
  documentWatchlist().collectionWatchlistItems()

fun FirebaseFirestore.collectionUsers() = collection(Collection.USERS)

fun FirebaseAuth.getLocalUser() = LocalUser(
  name = currentUser?.displayName,
  email = currentUser?.email,
  uid = currentUser?.uid,
)
