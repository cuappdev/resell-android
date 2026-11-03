package com.cornellappdev.resell.android.model

import com.cornellappdev.resell.android.model.api.Post
import com.cornellappdev.resell.android.model.chats.AvailabilityDocument
import com.cornellappdev.resell.android.model.chats.MeetingInfo
import com.cornellappdev.resell.android.model.chats.MeetingState
import com.cornellappdev.resell.android.model.chats.TransactionInfo
import com.cornellappdev.resell.android.util.justinMessages
import com.cornellappdev.resell.android.util.richieMessages
import com.google.firebase.Timestamp

/**
 * All the data needed to one chat conversation between two parties.
 */
data class Chat(
    val chatHistory: List<ChatMessageCluster> = listOf(richieMessages(5), justinMessages(3)),
)

/**
 * Returns the most recent [ChatMessageData] matching [predicate]. Returns null if nothing matches.
 */
fun Chat.mostRecentMessage(predicate: (ChatMessageData) -> Boolean): ChatMessageData? =
    chatHistory.flatMap { it.messages }.filter(predicate).maxByOrNull { it.timestamp }

/**
 * Returns the meeting from the most recent meeting message if it is confirmed.
 */
fun Chat.confirmedMeeting(): MeetingInfo? =
    mostRecentMessage { it.meetingInfo != null }?.meetingInfo
        ?.takeIf { it.state == MeetingState.CONFIRMED }

enum class MessageType {
    Image, Card, Message, Availability, State
}

/**
 * @param imageUrl Only used if [messageType] is [MessageType.Image].
 * @param availability Only used if [messageType] is [MessageType.Availability].
 * @param meetingInfo Only used if [messageType] is [MessageType.State].
 * @param post Currently unused.
 */
data class ChatMessageData(
    val timestamp: Timestamp,
    val id: String,
    val content: String,
    val senderId: String,
    val messageType: MessageType,
    val imageUrl: String = "",
    val availability: AvailabilityDocument? = null,
    val meetingInfo: MeetingInfo? = null,
    val transactionInfo: TransactionInfo? = null,
    val transactionId: String? = null,
    val post: Post? = null
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
