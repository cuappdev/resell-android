package com.cornellappdev.resell.android.model.chats

import com.google.firebase.Timestamp

data class ChatHeaderData(
    /**
     * The text of the most recent message.
     */
    val recentMessage: String,
    /**
     * Ex: 2024-10-26T23:37:43.433Z
     */
    val updatedAt: Timestamp,
    val read: Boolean,
    val name: String,
    val listingName: String,
    val listingId: String,
    /** URL */
    val imageUrl: String,
    val chatId: String,
)

data class RawChatHeaderData(
    val listingID: String,
    val buyerID: String,
    val sellerID: String,
    val userIDs: List<String>,
    val lastMessage: String,
    val updatedAt: Timestamp,
    val chatID: String,
)
