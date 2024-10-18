package com.cornellappdev.resell.android.ui.theme

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
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
val IconInactive = Color(0xFFBEBEBE)
val Stroke = Color(0xFFD6D6D6)
val Wash = Color(0xFFF4F4F4)
val Tint = Color(0x33000000)
val AppDev = Color(0xFF707070)
val Warning = Color(0xFFF20000)
val Venmo = Color(0xFF3D95CE)
val Overlay = Color(0xEEEDEDED)

val LoginBlurBrushStart = Brush.radialGradient(
    colors = listOf(
        Color(0x338F00FF),
        Color(0x008F00FF),
    ),
    center = Offset(0f, 0f),
    radius = 1400f
)

val LoginBlurBrushEnd = Brush.radialGradient(
    colors = listOf(
        Color(0x33FF7A00),
        Color(0x00FF13E7),
    ),
    center = Offset(0f, 0f),
    radius = 1400f
)

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

val ResellGradientLogo = Brush.linearGradient(
    colors = gradientList,
    start = Offset(200f, 0f),
    end = Offset(0f, 200f)
)

val ResellGradientDiagonal = Brush.linearGradient(
    colors = gradientList,
    start = Offset(50f, 50f),
    end = Offset.Zero
)

/**
 * Interpolates between two colors based on a given fraction.
 */
fun interpolateColorHSV(startColor: Color, endColor: Color, fraction: Float): Color {
    // Clamp the fraction to be between 0 and 1
    val clampedFraction = fraction.coerceIn(0f, 1f)

    // Convert start and end colors to HSV
    val startHSV = FloatArray(3)
    val endHSV = FloatArray(3)
    android.graphics.Color.colorToHSV(
        android.graphics.Color.argb(
            (startColor.alpha * 255).toInt(),
            (startColor.red * 255).toInt(),
            (startColor.green * 255).toInt(),
            (startColor.blue * 255).toInt()
        ),
        startHSV
    )
    android.graphics.Color.colorToHSV(
        android.graphics.Color.argb(
            (endColor.alpha * 255).toInt(),
            (endColor.red * 255).toInt(),
            (endColor.green * 255).toInt(),
            (endColor.blue * 255).toInt()
        ),
        endHSV
    )

    // Interpolate HSV values
    val hue = startHSV[0] + (endHSV[0] - startHSV[0]) * clampedFraction
    val saturation = startHSV[1] + (endHSV[1] - startHSV[1]) * clampedFraction
    val value = startHSV[2] + (endHSV[2] - startHSV[2]) * clampedFraction

    // Convert back to RGB
    val rgb = android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, value))

    return Color(
        red = ((rgb shr 16) and 0xFF) / 255f,
        green = ((rgb shr 8) and 0xFF) / 255f,
        blue = (rgb and 0xFF) / 255f,
        alpha = ((rgb shr 24) and 0xFF) / 255f
    )
}

@Composable
fun animateResellBrush(
    targetGradient: Boolean,
    animationSpec: AnimationSpec<Float> = tween(500),
    start: Offset = Offset(50f, 50f),
    end: Offset = Offset(0f, 0f),
): Brush {
    val position = animateFloatAsState(
        targetValue = if (targetGradient) 0f else 1f,
        label = "gradient",
        animationSpec = animationSpec
    )

    val gradient = listOf(
        interpolateColorHSV(gradientTop, IconInactive, position.value),
        interpolateColorHSV(gradientMiddle, IconInactive, position.value),
        interpolateColorHSV(gradientBottom, IconInactive, position.value)
    )

    return Brush.linearGradient(
        colors = gradient,
        start = start,
        end = end
    )
}
