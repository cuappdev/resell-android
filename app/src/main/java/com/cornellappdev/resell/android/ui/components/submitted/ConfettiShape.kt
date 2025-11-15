package com.cornellappdev.resell.android.ui.components.submitted


import androidx.compose.ui.graphics.Color


enum class ConfettiShape { CIRCLE, RECTANGLE }


data class ConfettiParticle(
    val x: Float,
    val size: Int,
    val color: Color,
    val shape: ConfettiShape,
    val rotation: Float,
    val speed: Float
)
