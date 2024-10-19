package com.cornellappdev.resell.android.model

import com.cornellappdev.resell.android.util.justinMessages
import com.cornellappdev.resell.android.util.richieMessages
import com.cornellappdev.resell.android.viewmodel.main.ChatViewModel.ChatType

data class Chat(
        val seller: String = "Unknown",
        val title: String = "Unknown",
        val chatId: Int,
        val chatType: ChatType = ChatType.Purchases,
        val chatHistory: List<ChatMessageCluster> = listOf(richieMessages(5), justinMessages(3)),
        val draftMessage: String = "",
        val draftImages: List<String> = listOf()

)

enum class MessageType {
        Image, Card, Message
}

data class ChatMessageData(
        val id: Int,
        val content: String,
        val timestamp: Long,
        val messageType: MessageType
)

data class ChatMessageCluster(
        val senderId: Int = 0,
        val senderImage: String?,
        val fromUser: Boolean = false, // Todo: Temporary
        val messages: List<ChatMessageData>
)

