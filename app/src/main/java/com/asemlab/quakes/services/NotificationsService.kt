package com.asemlab.quakes.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.asemlab.quakes.BuildConfig
import com.asemlab.quakes.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class NotificationsService : FirebaseMessagingService() {
    private val DEFAULT_CHANNEL_ID = "FIREBASE_NOTIFICATION"
    private val NOTIFICATION_ID = 1001

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        message.notification?.let {
            showNotification(it)
        }
    }

    private fun showNotification(it: RemoteMessage.Notification) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = Notification.Builder(this)
            .setContentTitle(it.title ?: "")
            .setContentText(it.body ?: "")
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.asset_7)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                DEFAULT_CHANNEL_ID,
                getString(R.string.default_notification_channel_id),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description =
                getString(R.string.default_notification_channel_desc)
            notificationManager.createNotificationChannel(channel)
            notification.setChannelId(DEFAULT_CHANNEL_ID)
        }

        notificationManager.notify(NOTIFICATION_ID, notification.build())

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if(BuildConfig.DEBUG)
            Log.d("FireBaseMessaging", token)
    }
}