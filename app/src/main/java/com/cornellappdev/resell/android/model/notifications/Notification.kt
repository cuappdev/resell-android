package com.cornellappdev.resell.android.model.messages

import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.abs

data class Notification(
    val id: Int,
    val title: String,
    val timestate: Long,
    val notificationType: List<NotificationType>,
    var unread: Boolean
) {
    fun timestamp(): String {
        val differenceInMillis =
            abs(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli() - timestate)
        val differenceInHours = differenceInMillis / (1000 * 60 * 60)
        return if (differenceInHours < 24) {
            "$differenceInHours hrs ago"
        } else {
            val differenceInDays = (differenceInHours / 24)
            "$differenceInDays days ago"
        }
    }
}

enum class NotificationType {
    Message, Request, Seller, Buyer
}
