package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.ptf.StarRating
import com.cornellappdev.resell.android.ui.theme.IconInactive
import com.cornellappdev.resell.android.ui.theme.ResellPurple

@Composable
fun ResellRatingBar(
    rating: StarRating?,
    onRatingChanged: (StarRating) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        for (star in StarRating.entries) {
            Image(
                painter = if (rating != null && star <= rating) painterResource(id = R.drawable.ic_selected_star) else painterResource(id = R.drawable.ic_unselected_star),
                contentDescription = "Star ${star.stars}",
                modifier = Modifier
                    .clickable {
                        onRatingChanged(star)
                    }
            )
        }
    }
}

@Composable
fun ResellStarRating(
    rating: Double,
    modifier: Modifier = Modifier,
    starSize: Dp = 12.dp,
) {
    val ratingText = if (rating % 1.0 == 0.0) rating.toInt().toString() else "%.1f".format(rating)
    Row(
        modifier = modifier.semantics { contentDescription = "$ratingText out of 5 stars" },
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(5) { index ->
            val fill = (rating - index).coerceIn(0.0, 1.0).toFloat()
            Box(modifier = Modifier.size(starSize)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_selected_star),
                    contentDescription = null,
                    tint = IconInactive,
                    modifier = Modifier.fillMaxSize()
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_selected_star),
                    contentDescription = null,
                    tint = ResellPurple,
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithContent {
                            clipRect(right = size.width * fill) {
                                this@drawWithContent.drawContent()
                            }
                        }
                )
            }
        }
    }
}
