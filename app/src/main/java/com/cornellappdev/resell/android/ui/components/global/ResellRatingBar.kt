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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.theme.IconInactive
import com.cornellappdev.resell.android.ui.theme.ResellPurple

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

@Composable
fun ResellStarRating(
    rating: Double,
    modifier: Modifier = Modifier,
    starSize: Dp = 12.dp,
    maxRating: Int = 5,
    filledColor: Color = ResellPurple,
    emptyColor: Color = IconInactive,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        for (i in 1..maxRating) {
            val fill = (rating - (i - 1)).coerceIn(0.0, 1.0).toFloat()
            Box(modifier = Modifier.size(starSize)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_selected_star),
                    contentDescription = null,
                    tint = emptyColor,
                    modifier = Modifier.fillMaxSize()
                )
                if (fill > 0f) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_selected_star),
                        contentDescription = null,
                        tint = filledColor,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(FractionalWidthShape(fill))
                    )
                }
            }
        }
    }
}

private class FractionalWidthShape(private val fraction: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline = Outline.Rectangle(Rect(0f, 0f, size.width * fraction, size.height))
}
