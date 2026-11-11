package com.cornellappdev.resell.android.ui.components.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.classes.ProfileReview
import com.cornellappdev.resell.android.ui.theme.IconInactive
import com.cornellappdev.resell.android.ui.theme.Padding
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.Secondary
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.shimmer

private val ReviewListPadding = 16.dp
private val ReviewSkeletonHeight = 88.dp
private const val SKELETON_COUNT = 3

@Composable
fun ResellReviewsScroll(
    reviews: List<ProfileReview>,
    modifier: Modifier = Modifier,
) {
    if (reviews.isEmpty()) {
        ReviewsEmptyState(modifier)
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = Padding.leftRight,
            end = Padding.leftRight,
            top = ReviewListPadding,
            bottom = 100.dp
        ),
        verticalArrangement = Arrangement.spacedBy(ReviewListPadding)
    ) {
        items(reviews) { review ->
            ReviewCard(review = review)
        }
    }
}

@Composable
private fun ReviewsEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_star_slash),
            contentDescription = null,
            tint = IconInactive
        )
        Text(text = "No Reviews Yet", style = Style.body1, color = Secondary)
        Text(
            text = "Reviews from buyers will appear here",
            style = Style.body2,
            color = IconInactive
        )
    }
}

@Composable
fun ResellLoadingReviewsScroll(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = Padding.leftRight, end = Padding.leftRight, top = ReviewListPadding),
        verticalArrangement = Arrangement.spacedBy(ReviewListPadding)
    ) {
        repeat(SKELETON_COUNT) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ReviewSkeletonHeight)
                    .clip(RoundedCornerShape(ReviewCardCornerRadius))
                    .shimmer()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ResellReviewsScrollPreview() = ResellPreview {
    ResellReviewsScroll(
        reviews = listOf(
            ProfileReview(
                id = "1",
                rating = 5,
                comment = "Super easy to meet up with and the item was exactly as described!",
                dateLabel = "Oct 12, 2026",
                reviewerName = "Ryan",
            ),
            ProfileReview(
                id = "2",
                rating = 3,
                comment = "",
                dateLabel = "Oct 3, 2026",
                reviewerName = "Anonymous",
            ),
        )
    )
}
