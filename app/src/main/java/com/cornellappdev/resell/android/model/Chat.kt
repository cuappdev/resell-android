package com.cornellappdev.resell.android.model

import com.cornellappdev.resell.android.viewmodel.main.ChatViewModel

/**
 * A product listing.
 */

data class Chat(
        val seller: String = "Unknown",
        val title: String = "Unknown",
        val chatId: Int,
        val chatType: ChatViewModel.ChatType = ChatViewModel.ChatType.Purchases
)

enum class MessageType {
        Image, Card, Message
}

data class ChatMessageData(
        val id: Int,               // Unique identifier for the message
        val content: String,          // Content of the message (text, URL for images, etc.)
        val senderId: Int,         // Identifier for the sender (could be a user ID)
        val timestamp: Long,          // Timestamp for when the message was sent
        val messageType: MessageType  // Type of the message (Image, Card, Message)
)
