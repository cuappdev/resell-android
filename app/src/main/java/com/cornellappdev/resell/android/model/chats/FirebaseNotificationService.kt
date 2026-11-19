package com.cornellappdev.resell.android.model.chats

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import java.net.HttpURLConnection
import java.net.URL
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
        Log.d("FCM", "Data payload: ${remoteMessage.data}")

        val data = remoteMessage.data
        val notification = remoteMessage.notification

        val type = (data["type"] ?: data["notificationType"] ?: "").lowercase()
        val rawSenderName = data["name"] ?: data["senderName"]
        val senderName = rawSenderName ?: "Someone"
        val messageBody = data["body"] ?: data["message"] ?: notification?.body ?: "sent you a message"
        val pfpUrl = data["pfp"] ?: data["profilePic"] ?: data["avatar"] ?: data["imageUrl"] ?: ""

        val isChatOrMessage = type.isEmpty() || type.contains("message") || type.contains("chat") || type.contains("msg")

        val title = if (isChatOrMessage) {
            "$senderName sent you a message"
        } else {
            data["title"] ?: notification?.title ?: "Resell"
        }

        sendNotification(
            title = title,
            messageBody = messageBody,
            name = rawSenderName,
            email = data["email"],
            pfp = pfpUrl,
            postJson = data["postJson"],
            isBuyer = data["isBuyer"]?.toBoolean() ?: false
        )
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
        CoroutineScope(Dispatchers.IO).launch {
            val notificationId = System.currentTimeMillis().toInt()
            val channelId = "chat_notifications_channel"

            // Intent to open when the notification is clicked
            val intent = Intent(this@FirebaseNotificationService, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("name", name)
                putExtra("email", email)
                putExtra("pfp", pfp)
                putExtra("postJson", postJson)
                putExtra("isBuyer", isBuyer)
            }

            val pendingIntent = PendingIntent.getActivity(
                this@FirebaseNotificationService, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )

            // Load profile picture bitmap if URL is provided
            var largeIconBitmap: Bitmap? = null
            if (!pfp.isNullOrBlank()) {
                try {
                    val url = URL(pfp)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()
                    val input = connection.inputStream
                    largeIconBitmap = BitmapFactory.decodeStream(input)
                } catch (e: Exception) {
                    Log.e("FirebaseNotificationService", "Failed to load profile picture for notification", e)
                }
            }

            // Build the notification
            val notificationBuilder = NotificationCompat.Builder(this@FirebaseNotificationService, channelId)
                .setSmallIcon(R.drawable.logo_resell)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            if (largeIconBitmap != null) {
                notificationBuilder.setLargeIcon(largeIconBitmap)
            }

            // Notification manager
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            // Create the notification channel for Android O+
            val channel = NotificationChannel(
                channelId,
                "Chat Messages",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)

            // Show the notification
            notificationManager.notify(notificationId, notificationBuilder.build())
        }
    }
}
