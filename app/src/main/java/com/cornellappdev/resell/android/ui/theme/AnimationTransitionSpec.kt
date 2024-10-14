package com.cornellappdev.resell.android.ui.theme

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith

val simpleFadeInOut: AnimatedContentTransitionScope<Any>.() -> ContentTransform = {
    fadeIn(
        animationSpec = tween(300, delayMillis = 0)
    ).togetherWith(
        fadeOut(
            animationSpec = tween(300)
        )
    )
}

val instantFadeInOut: AnimatedContentTransitionScope<Any>.() -> ContentTransform = {
    fadeIn(
        animationSpec = tween(0)
    ).togetherWith(
        fadeOut(
            animationSpec = tween(0)
        )
    )
}
