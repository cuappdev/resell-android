package com.cornellappdev.resell.android.util

import com.cornellappdev.resell.android.model.Listing
import com.cornellappdev.resell.android.model.UserInfo

import com.cornellappdev.resell.android.model.Chat
import com.cornellappdev.resell.android.model.ChatMessageCluster
import com.cornellappdev.resell.android.model.ChatMessageData
import com.cornellappdev.resell.android.model.MessageType

val richieListings = { count: Int ->
    List(count) {
        Listing(
            id = it,
            title = "title$it",
            image = richieUrl,
            price = "$$it.00",
            category = "category$it"
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
    bio = "bio bio bio"
)

val justinChats = { count: Int ->
    List(count) {
        Chat(
            chatId = -1
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