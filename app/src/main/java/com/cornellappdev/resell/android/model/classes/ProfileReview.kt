package com.cornellappdev.resell.android.model.classes

import com.cornellappdev.resell.android.model.api.TransactionReviewListItem
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

data class ProfileReview(
    val id: String,
    val rating: Int,
    val comments: String,
    val dateLabel: String,
    val reviewerName: String,
)

/**
 * Returns null for a review with no star rating.
 */
fun TransactionReviewListItem.toProfileReview(): ProfileReview? {
    val stars = stars ?: return null
    val buyer = transaction?.buyer
    return ProfileReview(
        id = id.orEmpty(),
        rating = stars.coerceIn(0, 5),
        comments = comments?.trim().orEmpty(),
        dateLabel = createdAt.toDateLabel(),
        reviewerName = listOfNotNull(buyer?.givenName, buyer?.username)
            .firstOrNull { it.isNotBlank() } ?: "Anonymous",
    )
}

private fun String?.toDateLabel(): String = runCatching {
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        .withZone(ZoneId.systemDefault())
        .format(Instant.parse(orEmpty()))
}.getOrDefault("")
