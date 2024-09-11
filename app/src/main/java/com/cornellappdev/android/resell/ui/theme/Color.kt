package com.cornellappdev.android.resell.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val ResellPurple = Color(0xFF9E70F6)

val Primary = Color(0xFF000000)
val Secondary = Color(0xFF4D4D4D)
val IconInactive = Color(0xFF4D4D4D)
val Stroke = Color(0xFFD6D6D6)
val Wash = Color(0xFFF4F4F4)
val Tint = Color(0x33000000)

private val gradientTop = Color(0xFFDF9856)
private val gradientMiddle = Color(0xFFAD68E3)
private val gradientBottom = Color(0xFFDE6CD3)

private val gradientList = listOf(
    gradientTop,
    gradientMiddle,
    gradientBottom
)

// TODO: offset probably wrong.
val ResellGradientVertical = Brush.linearGradient(
    colors = gradientList,
    start = Offset(0f, 100f),
    end = Offset.Zero
)

val ResellGradientDiagonal = Brush.linearGradient(
    colors = gradientList,
    start = Offset(100f, 100f),
    end = Offset.Zero
)
