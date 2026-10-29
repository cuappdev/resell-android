package com.cornellappdev.resell.android.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp

val LocalInfiniteShimmer = compositionLocalOf<Color> { error("No infinite loading provided") }

/**
 * Width of the window container, in Dp.
 */
@Composable
fun windowWidth(): Dp = with(LocalDensity.current) {
    LocalWindowInfo.current.containerSize.width.toDp()
}

/**
 * Height of the window container, in Dp.
 */
@Composable
fun windowHeight(): Dp = with(LocalDensity.current) {
    LocalWindowInfo.current.containerSize.height.toDp()
}
