package com.cornellappdev.resell.android.ui.theme

import android.content.res.Configuration
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.util.LocalInfiniteLoading

@Composable
fun ResellPreview(
    padding: Dp = 0.dp,
    backgroundColor: Color = Color.White,
    content: @Composable () -> Unit
) {
    // Create an infinite transition for animation
    val transition = rememberInfiniteTransition()

    // Animate a value from 0 to 1 infinitely
    val animatedValue = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = InfiniteRepeatableSpec(
            animation = keyframes {
                durationMillis = 2000
                0f at 0
                1f at 1000
                0f at 2000
            }
        ),
        label = "infinite loading"
    ).value

    CompositionLocalProvider(
        LocalInfiniteLoading provides animatedValue
    ) {
        Column(
            modifier = Modifier
                .background(backgroundColor)
                .padding(padding)
        ) {
            content()
        }
    }
}
