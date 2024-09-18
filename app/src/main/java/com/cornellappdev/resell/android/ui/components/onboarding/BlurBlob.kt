package com.cornellappdev.resell.android.ui.components.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.theme.LoginBlurBrushStart


enum class Corner {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
}

@Composable
fun BlurBlob(
    modifier: Modifier = Modifier,
    brush: Brush = LoginBlurBrushStart,
    corner: Corner,
) {
    val scaleX = when (corner) {
        Corner.TOP_LEFT -> 1f
        Corner.TOP_RIGHT -> -1f
        Corner.BOTTOM_LEFT -> 1f
        Corner.BOTTOM_RIGHT -> -1f
    }

    val scaleY = when (corner) {
        Corner.TOP_LEFT -> 1f
        Corner.TOP_RIGHT -> 1f
        Corner.BOTTOM_LEFT -> -1f
        Corner.BOTTOM_RIGHT -> -1f
    }

    Box(
        modifier
            .graphicsLayer(scaleX = scaleX, scaleY = scaleY)
            .background(brush = brush)
            .size(500.dp)
    ) {}
}

@Preview(apiLevel = 34)
@Composable
private fun PreviewBlurBlob() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        BlurBlob(
            modifier = Modifier.align(Alignment.TopStart),
            corner = Corner.TOP_LEFT
        )

        BlurBlob(
            modifier = Modifier.align(Alignment.TopEnd),
            corner = Corner.TOP_RIGHT
        )

        BlurBlob(
            modifier = Modifier.align(Alignment.BottomStart),
            corner = Corner.BOTTOM_LEFT
        )

        BlurBlob(
            modifier = Modifier.align(Alignment.BottomEnd),
            corner = Corner.BOTTOM_RIGHT
        )
    }
}
