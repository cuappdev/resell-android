package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.ptf.StarRating

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