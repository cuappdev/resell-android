package com.cornellappdev.resell.android.ui.components.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.cornellappdev.resell.android.model.classes.UserReview
import com.cornellappdev.resell.android.ui.components.global.ResellStarRating
import com.cornellappdev.resell.android.ui.theme.IconInactive
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.Stroke
import com.cornellappdev.resell.android.ui.theme.Style

@Composable
fun ReviewCard(
    review: UserReview,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, shape)
            .border(BorderStroke(1.dp, Stroke), shape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = review.reviewerName, style = Style.title3)

            if (review.dateLabel.isNotBlank()) {
                Text(text = "•", style = Style.body2, modifier = Modifier.padding(horizontal = 4.dp))
                Text(text = review.dateLabel, style = Style.body2, color = IconInactive)
            }

            Spacer(Modifier.weight(1f))

            ResellStarRating(rating = review.rating.toDouble(), starSize = 20.dp)
        }

        if (review.comments.isNotBlank()) {
            Text(
                text = review.comments,
                style = Style.body2,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReviewCardPreview() = ResellPreview(padding = 16.dp) {
    ReviewCard(
        review = UserReview(
            id = "1",
            rating = 4,
            comments = "Super easy to meet up with and the item was exactly as described!",
            dateLabel = "Oct 12, 2026",
            reviewerName = "Jin",
        )
    )
}
