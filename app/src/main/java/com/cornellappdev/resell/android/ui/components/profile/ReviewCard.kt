package com.cornellappdev.resell.android.ui.components.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.model.classes.ProfileReview
import com.cornellappdev.resell.android.ui.components.global.ResellStarRating
import com.cornellappdev.resell.android.ui.theme.IconInactive
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.Stroke
import com.cornellappdev.resell.android.ui.theme.Style

internal val ReviewCardCornerRadius = 16.dp
private const val COMMENT_MAX_LINES = 3

@Composable
fun ReviewCard(
    review: ProfileReview,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(ReviewCardCornerRadius)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, shape)
            .border(BorderStroke(1.dp, Stroke), shape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = review.reviewerName,
                    style = Style.title3,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                if (review.dateLabel.isNotBlank()) {
                    Text(
                        text = "•",
                        style = Style.body2,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Text(
                        text = review.dateLabel,
                        style = Style.body2,
                        color = IconInactive,
                        maxLines = 1
                    )
                }
            }

            ResellStarRating(rating = review.rating.toDouble(), starSize = 20.dp)
        }

        // Only shows 3 lines of a review (rest is cut off)
        if (review.comments.isNotBlank()) {
            Text(
                text = review.comments,
                style = Style.body2,
                maxLines = COMMENT_MAX_LINES,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReviewCardPreview() = ResellPreview(padding = 16.dp) {
    ReviewCard(
        review = ProfileReview(
            id = "1",
            rating = 4,
            comments = "Super easy to meet up with and the item was exactly as described!",
            dateLabel = "Oct 12, 2026",
            reviewerName = "Ryan",
        )
    )
}
