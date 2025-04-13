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

@Composable
fun ResellRatingBar(
    rating: Int,
    onRatingChanged: (Int) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        for (i in 1..5) {
            Image(
                painter = if (i <= rating) painterResource(id = R.drawable.ic_selected_star) else painterResource(id = R.drawable.ic_unselected_star),
                contentDescription = "Star $i",
                modifier = Modifier
                    .clickable {
                        onRatingChanged(i)
                    }
            )
        }
    }
}