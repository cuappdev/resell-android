package com.cornellappdev.resell.android.model.classes

import com.cornellappdev.resell.android.model.api.TransactionReviewListItem
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

data class ProfileReview(
    val id: String,
    val rating: Int,
    val comment: String,
    val dateLabel: String,
    val reviewerName: String,
)

/**
 * Returns null for a review with no star rating.
 *
 * @param fallbackId Used when the backend sends no id, so such reviews stay distinct in lists.
 */
fun TransactionReviewListItem.toProfileReview(fallbackId: String): ProfileReview? {
    val stars = stars ?: return null
    val buyer = transaction?.buyer
    return ProfileReview(
        id = id?.takeIf { it.isNotEmpty() } ?: fallbackId,
        rating = stars.coerceIn(0, 5),
        comment = comments?.trim().orEmpty(),
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
