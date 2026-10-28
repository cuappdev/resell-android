package com.cornellappdev.resell.android.util

import com.cornellappdev.resell.android.model.ChatMessageCluster
import com.cornellappdev.resell.android.model.ChatMessageData
import com.cornellappdev.resell.android.model.MessageType
import com.cornellappdev.resell.android.model.classes.UserInfo
import com.google.firebase.Timestamp


const val richieUrl =
    "https://media.licdn.com/dms/image/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/0/1704329714345?e=2147483647&v=beta&t=Kq7ex1pKyiifjOpuNIojeZ8f4dXjEAsNSpkJDXBwjxc"

val richieUserInfo = UserInfo(
    name = "Richie",
    imageUrl = richieUrl,
    netId = "richie",
    username = "richie",
    venmoHandle = "richie",
    bio = "bio bio bio",
    id = "richie",
    email = "richie@sun.com"
)

val richieMessages = { count: Int ->
    ChatMessageCluster(
        senderId = count.toString(),
        senderImage = "https://media.licdn.com/dms/image/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/0/1704329714345?e=2147483647&v=beta&t=Kq7ex1pKyiifjOpuNIojeZ8f4dXjEAsNSpkJDXBwjxc",
        fromUser = false,
        messages = List(count) {
            if (it == 1) {
                ChatMessageData(
                    timestamp = Timestamp(0, 0),
                    "https://media.licdn.com/dms/image/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/0/1704329714345?e=2147483647&v=beta&t=Kq7ex1pKyiifjOpuNIojeZ8f4dXjEAsNSpkJDXBwjxc",
                    "deajdaejk",
                    messageType = MessageType.Card,
                    senderId = ""
                )
            } else {
                ChatMessageData(
                    timestamp = Timestamp(0, 0),
                    "HELP",
                    "feajkdna",
                    messageType = MessageType.Message,
                    senderId = ""
                )
            }
        },
        senderName = "My man"
    )
}

val justinMessages = { count: Int ->
    ChatMessageCluster(
        senderId = count.toString(),
        senderImage = "https://media.licdn.com/dms/image/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/0/1704329714345?e=2147483647&v=beta&t=Kq7ex1pKyiifjOpuNIojeZ8f4dXjEAsNSpkJDXBwjxc",
        fromUser = true,
        messages = List(count) {
            when (it) {
                1 -> ChatMessageData(
                    timestamp = Timestamp(0, 0),
                    "Lia",
                    "feaojfea",
                    messageType = MessageType.Availability,
                    senderId = ""
                )

                2 ->
                    ChatMessageData(
                        timestamp = Timestamp(0, 0),
                        "Lia",
                        "00efa",
                        messageType = MessageType.State,
                        senderId = ""
                    )

                else ->
                    ChatMessageData(
                        timestamp = Timestamp(0, 0), "HELP", "FEADea",
                        messageType = MessageType.Message,
                        senderId = ""
                    )
            }
        },
        senderName = "God"
    )
}
