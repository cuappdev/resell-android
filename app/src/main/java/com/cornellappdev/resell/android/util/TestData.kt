package com.cornellappdev.resell.android.util

import com.cornellappdev.resell.android.model.Listing

import com.cornellappdev.resell.android.model.Chat
import com.cornellappdev.resell.android.model.ChatMessageData
import com.cornellappdev.resell.android.model.MessageType

val richieListings = { count: Int ->
    List(count) {
        Listing(
            id = it,
            title = "title$it",
            image = "https://media.licdn.com/dms/image/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/0/1704329714345?e=2147483647&v=beta&t=Kq7ex1pKyiifjOpuNIojeZ8f4dXjEAsNSpkJDXBwjxc",
            price = "$$it.00",
            category = "category$it"
        )
    }
}

val justinChats = { count: Int ->
    List(count) {
        Chat(
            chatId  = 0
        )
    }
}

val richieMessages = { count: Int ->
    List(count) {
        if(it == 1){
            ChatMessageData(0, "https://media.licdn.com/dms/image/D4E03AQGOCNNbxGtcjw/profile-displayphoto-shrink_200_200/0/1704329714345?e=2147483647&v=beta&t=Kq7ex1pKyiifjOpuNIojeZ8f4dXjEAsNSpkJDXBwjxc", 0, 0.toLong(), MessageType.Card)
        } else {
            ChatMessageData(0, "HELP", 0, 0.toLong(), MessageType.Message)
        }

    }
}