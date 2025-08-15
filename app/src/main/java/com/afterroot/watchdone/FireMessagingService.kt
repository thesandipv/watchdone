/*
 * Copyright (C) 2020-2025 Sandip Vaghela
 * SPDX-License-Identifier: Apache-2.0
 */
package com.afterroot.watchdone

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import app.tivi.util.Logger
import com.afterroot.data.utils.FirebaseUtils
import com.afterroot.utils.getMaterialColor
import com.afterroot.watchdone.base.Collection
import com.afterroot.watchdone.base.Field
import com.afterroot.watchdone.ui.MainActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.afterroot.watchdone.resources.R as CommonR

/**
 * FCM Service that will be used only if app is in foreground state.
 */
@Suppress("EXPERIMENTAL_API_USAGE")
@AndroidEntryPoint
class FireMessagingService : FirebaseMessagingService() {

  @Inject lateinit var firebaseUtils: FirebaseUtils

  @Inject lateinit var firestore: FirebaseFirestore

  @Inject lateinit var logger: Logger

  override fun onNewToken(token: String) {
    super.onNewToken(token)
    logger.d { "onNewToken: NEW_TOKEN $token" }
    updateToken(token)
  }

  private fun updateToken(token: String) {
    try {
      if (firebaseUtils.isUserSignedIn) {
        firestore.collection(Collection.USERS)
          .document(firebaseUtils.uid)
          .update(Field.FCM_ID, token)
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    if (remoteMessage.notification != null && remoteMessage.data.isNotEmpty()) {
      sendNotification(
        message = remoteMessage.notification!!.body!!,
        url = remoteMessage.data["link"],
        channelId = remoteMessage.notification!!.channelId,
        channelName = remoteMessage.data["cname"],
        title = remoteMessage.notification?.title,
      )
    }
  }

  @SuppressLint("UnspecifiedImmutableFlag")
  private fun sendNotification(
    message: String,
    url: String? = "",
    channelId: String? = getString(CommonR.string.fcm_channel_id),
    channelName: String? = getString(CommonR.string.fcm_channel_default),
    title: String? = getString(R.string.app_name),
  ) {
    val intent: Intent
    if (url!!.isEmpty()) {
      intent = Intent(this, MainActivity::class.java)
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    } else {
      intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
      }
    }
    val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      PendingIntent.getActivity(
        this,
        0,
        intent,
        PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE,
      )
    } else {
      PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
    }
    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val notificationBuilder =
      NotificationCompat.Builder(this, channelId ?: getString(CommonR.string.fcm_channel_id))
        .setSmallIcon(CommonR.drawable.ic_stat_main)
        .setContentTitle(title ?: getString(R.string.app_name))
        .setContentText(message)
        .setAutoCancel(true)
        .setColor(this.getMaterialColor(com.google.android.material.R.attr.colorSecondary))
        .setSound(defaultSoundUri)
        .setContentIntent(pendingIntent)

    val notificationManager =
      getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        channelId ?: getString(CommonR.string.fcm_channel_id),
        channelName ?: getString(CommonR.string.fcm_channel_default),
        NotificationManager.IMPORTANCE_DEFAULT,
      )
      notificationManager.createNotificationChannel(channel)
    }

    notificationManager.notify(0, notificationBuilder.build())
  }
}
