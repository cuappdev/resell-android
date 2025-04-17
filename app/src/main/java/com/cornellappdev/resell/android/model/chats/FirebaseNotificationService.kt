package com.cornellappdev.resell.android.model.chats

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.cornellappdev.resell.android.MainActivity
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.login.FireStoreRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseNotificationService : FirebaseMessagingService() {

    @Inject
    lateinit var fireStoreRepository: FireStoreRepository

    @Inject
    lateinit var userInfoRepository: UserInfoRepository

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Log the incoming message
        Log.d("FCM", "Message received from: ${remoteMessage.from}")

        // Check if the message contains a notification payload
        remoteMessage.notification?.let {
            val title = it.title ?: "Notification Title"
            val body = it.body ?: "Notification Body"

            sendNotification(
                title, body,
                name = remoteMessage.data["name"],
                email = remoteMessage.data["email"],
                pfp = remoteMessage.data["pfp"],
                postJson = remoteMessage.data["postJson"],
                isBuyer = remoteMessage.data["isBuyer"].toBoolean()
            )
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")
    }

    private fun sendNotification(
        title: String,
        messageBody: String,
        name: String? = null,
        email: String? = null,
        pfp: String? = null,
        postJson: String? = null,
        isBuyer: Boolean? = null
    ) {
        val notificationId = System.currentTimeMillis().toInt()
        val channelId = "default_channel_id"

        // Intent to open when the notification is clicked
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Add your data as extras here
            putExtra("name", name)
            putExtra("email", email)
            putExtra("pfp", pfp)
            putExtra("postJson", postJson)
            putExtra("isBuyer", isBuyer)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo_resell) // Replace with your app's notification icon
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Notification manager
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create the notification channel for Android O+
        val channel = NotificationChannel(
            channelId,
            "Default Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        // Show the notification
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
