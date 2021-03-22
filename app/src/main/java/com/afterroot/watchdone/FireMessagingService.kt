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
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.afterroot.watchdone.base.Collection
import com.afterroot.watchdone.base.Field
import com.afterroot.watchdone.ui.MainActivity
import com.afterroot.watchdone.utils.FirebaseUtils
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

/**
 * FCM Service that will be used only if app is in foreground state.
 */
@Suppress("EXPERIMENTAL_API_USAGE")
class FireMessagingService : FirebaseMessagingService(), KoinComponent {

    private val _tag = "FireMessagingService"
    private val firebaseUtils: FirebaseUtils by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e(_tag, "NEW_TOKEN $token")
        updateToken(token)
    }

    private fun updateToken(token: String) {
        try {
            if (firebaseUtils.isUserSignedIn) {
                firebaseUtils.uid?.let {
                    get<FirebaseFirestore>().collection(Collection.USERS)
                        .document(it)
                        .update(Field.FCM_ID, token)
                }
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
                title = remoteMessage.notification?.title
            )
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun sendNotification(
        message: String,
        url: String? = "",
        channelId: String? = getString(R.string.fcm_channel_id),
        channelName: String? = getString(R.string.fcm_channel_default),
        title: String? = getString(R.string.app_name)
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
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        }
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder =
            NotificationCompat.Builder(this, channelId ?: getString(R.string.fcm_channel_id))
                .setSmallIcon(R.drawable.ic_stat_main)
                .setContentTitle(title ?: getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(this, R.color.color_secondary))
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId ?: getString(R.string.fcm_channel_id),
                channelName ?: getString(R.string.fcm_channel_default),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}
