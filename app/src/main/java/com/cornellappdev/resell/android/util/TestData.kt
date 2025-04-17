package com.cornellappdev.resell.android.util

import com.cornellappdev.resell.android.model.Chat
import com.cornellappdev.resell.android.model.ChatMessageCluster
import com.cornellappdev.resell.android.model.ChatMessageData
import com.cornellappdev.resell.android.model.MessageType
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.UserInfo
import com.google.firebase.Timestamp

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

val fakeRichieListings = { count: Int ->
    List(count) {
        Listing(
            images = listOf("https://kohantextilejournal.com/wp-content/uploads/2020/11/Mr.-Ruizhe-Sun-China-768x384.jpg"),
            title = "title$it",
            price = "$$it.00",
            categories = listOf("category$it"),
            id = it.toString(),
            description = "Hello! I didn't sell this.",
            user = richieUserInfo
        )
    }
}

val tallRichieListings = { count: Int ->
    List(count) {
        Listing(
            images = listOf("https://images.squarespace-cdn.com/content/v1/558a10ebe4b09c778262786f/1557230577805-U5SJ6EPAWZMYGGTDEAWY/Richie-Moriarty-B46A2378-20.jpg"),
            title = "title$it",
            price = "$$it.00",
            categories = listOf("category$it"),
            id = it.toString(),
            description = "Hello! I didn't sell this.",
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
    id = "richie",
    email = "richie@sun.com"
)

val justinChats = { count: Int ->
    List(count) {
        Chat(
        )
    }
}

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
            if (it == 1) {
                ChatMessageData(
                    timestamp = Timestamp(0, 0),
                    "Lia",
                    "feaojfea",
                    messageType = MessageType.Availability,
                    senderId = ""
                )
            } else if (it == 2) {
                ChatMessageData(
                    timestamp = Timestamp(0, 0),
                    "Lia",
                    "00efa",
                    messageType = MessageType.State,
                    senderId = ""
                )
            } else {
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
