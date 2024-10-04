package com.cornellappdev.resell.android.ui.components.main

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import com.cornellappdev.resell.android.util.clickableNoIndication

@Composable
fun ShadeOverlay(
    onTapped: () -> Unit,
    visible: Boolean,
) {
    val currentVisibility = animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        label = "shade"
    )

    if (currentVisibility.value > 0f) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clickableNoIndication { onTapped() }
                .alpha(currentVisibility.value * 0.3f),
            color = Color.Black,
        ) {}
    }
}
