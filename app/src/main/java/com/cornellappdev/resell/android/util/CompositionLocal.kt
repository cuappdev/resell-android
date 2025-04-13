package com.cornellappdev.resell.android.util

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalInfiniteShimmer = compositionLocalOf<Color> { error("No infinite loading provided") }
