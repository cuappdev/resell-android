package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.cornellappdev.resell.android.ui.theme.Stroke
import com.cornellappdev.resell.android.ui.theme.Wash
import com.cornellappdev.resell.android.util.shimmer

@Composable
fun ResellLoadingCard(
    modifier: Modifier = Modifier,
    small: Boolean
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val width = 0.5f * screenWidth
    val scale = 1.0 / 173.0 * width
    val height = if (small) 144.73 * scale else 246.4 * scale
    val contentHeight = if(small) 111.73*scale else 213.4 * scale
    Column(
        modifier = modifier
            .widthIn(max = width)
            .fillMaxWidth()
            .clip(RoundedCornerShape(size = 8.dp))
            .background(Color.White)
            .border(width = 1.dp, color = Stroke, shape = RoundedCornerShape(8.dp))
            .heightIn(max = height)
    ) {
        Box(
            modifier = modifier
                .heightIn(max = contentHeight)
                .fillMaxSize()
                .background(
                    Wash
                )
//                .shimmer()
        )

        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LoadingText(106.0f, scale)
            LoadingText(45.0f, scale)
        }
    }
}

@Composable
private fun LoadingText(width: Float, scale: Dp) {
    Column(
        modifier = Modifier
            .widthIn(max = width * scale)
            .heightIn(max = 17.0 * scale)
            .fillMaxWidth()
            .clip(RoundedCornerShape(size = 100.dp))
            .background(Wash)
//            .shimmer()
            .border(width = 3.dp, color = Color.White, shape = RoundedCornerShape(100.dp))
            .fillMaxHeight()
    ) { }

}

@Preview
@Composable
private fun PreviewResellSmallLoadingCard() {
    ResellLoadingCard(modifier = Modifier, small = true)
}

@Preview
@Composable
private fun PreviewResellBigLoadingCard() {
    ResellLoadingCard(modifier = Modifier, small = false)
}