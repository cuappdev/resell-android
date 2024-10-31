package com.cornellappdev.resell.android.util

import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.UserInfo

import com.cornellappdev.resell.android.model.messages.Chat
import com.cornellappdev.resell.android.model.messages.ChatMessageCluster
import com.cornellappdev.resell.android.model.messages.ChatMessageData
import com.cornellappdev.resell.android.model.messages.MessageType
import com.cornellappdev.resell.android.model.messages.Notification
import com.cornellappdev.resell.android.model.messages.NotificationType
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.random.Random

val richieListings = { count: Int ->
    List(count) {
        Listing(
            id = it.toString(),
            title = "title$it",
            images = listOf(richieUrl),
            price = "$$it.00",
            categories = listOf("category$it"),
            description = "Hello! I need to sell this.",
            user = richieUserInfo
        )
    }
}

val richieUrl =
    "https://media.licdn.com/dms/image/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/0/1704329714345?e=2147483647&v=beta&t=Kq7ex1pKyiifjOpuNIojeZ8f4dXjEAsNSpkJDXBwjxc"

val tallUrl =
    "https://preview.redd.it/rmcg6gmzpa221.jpg?width=640&crop=smart&auto=webp&s=fae5cce02314436e1407cff714894dde0e40ddab"

val longUrl = "https://i.imgflip.com/6o9hva.jpg"

val richieUserInfo = UserInfo(
    name = "Richie",
    imageUrl = richieUrl,
    netId = "richie",
    username = "richie",
    venmoHandle = "richie",
    bio = "bio bio bio",
    id = "richie"
)

val justinChats = { count: Int ->
    List(count) {
        Chat(
            chatId = -1
        )
    }
}

fun getRandomPastTime(): Long {
    // Define the range in milliseconds
    val minTimeOffset = 1.0 * 60 * 60 * 1000 // 1 hour in milliseconds
    val maxTimeOffset = 40.0 * 24 * 60 * 60 * 1000 // 40 days in milliseconds

    // Generate a random offset within the range
    val randomOffset = Random.nextLong(minTimeOffset.toLong(), maxTimeOffset.toLong())

    // Subtract the random offset from the current time to get a past time
    return LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli() - randomOffset
}

val richieNotifications = { count: Int ->
    List(count) {
        Notification(
            id = (System.currentTimeMillis() * Random.nextInt(1000, 9999)).toInt(),
            title = "richie is interested in buying AppDev",
            timestate = getRandomPastTime(),
            notificationType = listOf(NotificationType.Message, NotificationType.Buyer),
            unread = Random.nextBoolean(),
        )
    }
}

val richieMessages = { count: Int ->
    ChatMessageCluster(
        senderId = count,
        senderImage = "https://media.licdn.com/dms/image/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/0/1704329714345?e=2147483647&v=beta&t=Kq7ex1pKyiifjOpuNIojeZ8f4dXjEAsNSpkJDXBwjxc",
        fromUser = false,
        messages = List(count) {
            if (it == 1) {
                ChatMessageData(
                    0,
                    "https://media.licdn.com/dms/image/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/0/1704329714345?e=2147483647&v=beta&t=Kq7ex1pKyiifjOpuNIojeZ8f4dXjEAsNSpkJDXBwjxc",
                    0.toLong(),
                    MessageType.Card
                )
            } else {
                ChatMessageData(0, "HELP", 0.toLong(), MessageType.Message)
            }
        }
    )
}

val justinMessages = { count: Int ->
    ChatMessageCluster(
        senderId = count,
        senderImage = "https://media.licdn.com/dms/image/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/0/1704329714345?e=2147483647&v=beta&t=Kq7ex1pKyiifjOpuNIojeZ8f4dXjEAsNSpkJDXBwjxc",
        fromUser = true,
        messages = List(count) {
            if (it == 1) {
                ChatMessageData(
                    0,
                    "Lia",
                    0.toLong(),
                    MessageType.Availability
                )
            } else if (it == 2) {
                ChatMessageData(
                    0,
                    "Lia",
                    0.toLong(),
                    MessageType.State
                )
            } else {
                ChatMessageData(0, "HELP", 0.toLong(), MessageType.Message)
            }
        }
    )
}