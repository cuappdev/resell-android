package com.cornellappdev.resell.android.ui.components.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
    var expanded by rememberSaveable { mutableStateOf(false) }
    var isTruncated by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
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

        // A comment cut off at 3 lines can be tapped to expand and collapse.
        if (review.comment.isNotBlank()) {
            Text(
                text = review.comment,
                style = Style.body2,
                maxLines = if (expanded) Int.MAX_VALUE else COMMENT_MAX_LINES,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { if (!expanded) isTruncated = it.hasVisualOverflow },
                modifier = Modifier.clickable(
                    enabled = isTruncated,
                    onClickLabel = if (expanded) "Collapse review" else "Expand review"
                ) { expanded = !expanded }
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
            comment = "Super easy to meet up with and the item was exactly as described!",
            dateLabel = "Oct 12, 2026",
            reviewerName = "Ryan",
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun ReviewCardLongCommentPreview() = ResellPreview(padding = 16.dp) {
    ReviewCard(
        review = ProfileReview(
            id = "2",
            rating = 5,
            comment = "Super easy to meet up with and the item was exactly as described! " +
                    "They replied quickly, were on time, and even helped carry the item to my " +
                    "dorm. Would definitely buy from them again and recommend to anyone looking " +
                    "for great deals. W Caleb!",
            dateLabel = "Oct 12, 2026",
            reviewerName = "Ryan",
        )
    )
}
