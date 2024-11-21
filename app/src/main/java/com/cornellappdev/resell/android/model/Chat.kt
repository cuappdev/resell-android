package com.cornellappdev.resell.android.model

import com.cornellappdev.resell.android.util.justinMessages
import com.cornellappdev.resell.android.util.richieMessages

/**
 * All the data needed to one chat conversation between two parties.
 */
data class Chat(
    val chatHistory: List<ChatMessageCluster> = listOf(richieMessages(5), justinMessages(3)),
)

enum class MessageType {
    Image, Card, Message, Availability, State
}

enum class MeetingProposalState {
    UserProposal, OtherProposal, UserDecline, OtherDecline
}

data class ChatMessageData(
    private val timestampString: String,
    val id: String,
    val content: String,
    val messageType: MessageType
)

data class ChatMessageCluster(
    val senderId: String,
    val senderImage: String?,
    val fromUser: Boolean = false,
    val messages: List<ChatMessageData>
)
