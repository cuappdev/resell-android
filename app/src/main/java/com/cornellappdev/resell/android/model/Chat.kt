package com.cornellappdev.resell.android.model

import com.cornellappdev.resell.android.model.api.Post
import com.cornellappdev.resell.android.model.chats.AvailabilityDocument
import com.cornellappdev.resell.android.model.chats.MeetingInfo
import com.cornellappdev.resell.android.util.justinMessages
import com.cornellappdev.resell.android.util.richieMessages
import com.google.firebase.Timestamp

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

/**
 * @param imageUrl Only used if [messageType] is [MessageType.Image].
 * @param post Only used if [messageType] is [MessageType.Card].
 * @param availability Only used if [messageType] is [MessageType.Availability].
 * @param meetingInfo Only used if [messageType] is [MessageType.State].
 */
data class ChatMessageData(
    val timestamp: Timestamp,
    val id: String,
    val content: String,
    val senderEmail: String,
    val messageType: MessageType,
    val imageUrl: String = "",
    val post: Post? = null,
    val availability: AvailabilityDocument? = null,
    val meetingInfo: MeetingInfo? = null,
) {
    /**
     * Timestamp in the form "(X)X:XX AM/PM"
     */
    val timestampString: String
        get() {
            val date = timestamp.toDate()
            val formatter = java.text.SimpleDateFormat("h:mm a", java.util.Locale.US)
            return formatter.format(date)
        }
}

data class ChatMessageCluster(
    val senderId: String,
    val senderImage: String?,
    val senderName: String?,
    val fromUser: Boolean = false,
    val messages: List<ChatMessageData>
)
