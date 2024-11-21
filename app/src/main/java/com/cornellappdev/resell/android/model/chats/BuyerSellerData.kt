package com.cornellappdev.resell.android.model.chats

import com.cornellappdev.resell.android.model.api.Post

data class BuyerSellerData(
    private val item: Post,
    /**
     * The text of the most recent message.
     */
    val recentMessage: String,
    /**
     * Ex: "2024-10-26T23:37:43.433Z"
     */
    val recentMessageTime: String,
    /**
     * Email address of the most recent sender
     */
    val recentSender: String,
    val confirmedTime: String,
    val confirmedViewed: Boolean,
    val name: String,
    /** URL */
    val image: String,
    val viewed: Boolean
) {
    val listing
        get() = item.toListing()
}
