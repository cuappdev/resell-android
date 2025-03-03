package com.cornellappdev.resell.android.util

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

val LocalInfiniteShimmer = compositionLocalOf<Color> { error("No infinite loading provided") }

@SuppressLint("ComposableNaming")
@Composable
fun Modifier.shimmer(): Modifier {
    val infiniteShimmer = LocalInfiniteShimmer.current
    return this.background(infiniteShimmer)
}
