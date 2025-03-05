package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.Stroke
import com.cornellappdev.resell.android.util.shimmer

@Composable
fun ResellLoadingCard(
    modifier: Modifier = Modifier,
    small: Boolean
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val maxWidth = 0.5f * screenWidth
    val contentHeight = if (small) 112.dp else 213.dp
    Column(
        modifier = modifier
            .widthIn(max = maxWidth)
            .fillMaxWidth()
            .clip(RoundedCornerShape(size = 8.dp))
            .background(color = Color.White)
            .border(width = 1.dp, color = Stroke, shape = RoundedCornerShape(8.dp))
    ) {
        Box(
            modifier = Modifier
                .height(contentHeight)
                .fillMaxWidth()
                .shimmer()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LoadingText(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(6.dp))
            LoadingText(modifier = Modifier.width(45.dp))
        }
    }
}

@Composable
private fun LoadingText(modifier: Modifier) {
    Box(
        modifier = modifier
            .height(17.dp)
            .clip(RoundedCornerShape(size = 100.dp))
            .shimmer()
            .border(width = 3.dp, color = Color.White, shape = RoundedCornerShape(100.dp))
    )
}

@Preview
@Composable
private fun PreviewResellSmallLoadingCard() = ResellPreview {
    ResellLoadingCard(modifier = Modifier, small = true)
}

@Preview
@Composable
private fun PreviewResellBigLoadingCard() = ResellPreview {
    ResellLoadingCard(modifier = Modifier, small = false)
}