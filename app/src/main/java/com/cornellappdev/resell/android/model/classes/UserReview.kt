package com.cornellappdev.resell.android.model.classes

import com.cornellappdev.resell.android.model.api.TransactionReviewListItem
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class UserReview(
    val id: String,
    val rating: Int,
    val comments: String,
    val dateLabel: String,
    val reviewerName: String,
)

fun TransactionReviewListItem.toUserReview(): UserReview {
    val buyer = transaction?.buyer
    val name = buyer?.givenName?.takeIf { it.isNotBlank() }
        ?: buyer?.username?.takeIf { it.isNotBlank() }
        ?: ""
    return UserReview(
        id = id ?: "",
        rating = (stars ?: 0).coerceIn(0, 5),
        comments = comments?.trim() ?: "",
        dateLabel = createdAt.toDateLabel(),
        reviewerName = name.ifBlank { "Anonymous" },
    )
}

private fun String?.toDateLabel(): String {
    if (isNullOrBlank()) return ""
    return try {
        DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
            .withZone(ZoneId.systemDefault())
            .format(Instant.parse(this))
    } catch (e: Exception) {
        ""
    }
}
