package com.cornellappdev.android.resell.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

object Style {
    /**
     * The Resell title/brand text style.
     */
    val resellBrand = TextStyle(
        fontSize = 32.sp,
        fontFamily = rubikFamily, // TODO: Wrong font.
        fontWeight = FontWeight(400),
        brush = ResellGradientVertical,
    )

    val body1 = TextStyle(
        fontSize = 18.sp,
        fontFamily = rubikFamily,
        fontWeight = FontWeight(400),
        color = Color.Black,
    )

    val body2 = TextStyle(
        fontSize = 16.sp,
        fontFamily = rubikFamily,
        fontWeight = FontWeight(400),
        color = Color.Black,
    )

    val heading1 = TextStyle(
        fontSize = 32.sp,
        fontFamily = rubikFamily,
        fontWeight = FontWeight(500),
        color = Color.Black,
    )

    val heading2 = TextStyle(
        fontSize = 22.sp,
        fontFamily = rubikFamily,
        fontWeight = FontWeight(500),
        color = Color.Black,
    )

    val heading3 = TextStyle(
        fontSize = 20.sp,
        fontFamily = rubikFamily,
        fontWeight = FontWeight(500),
        color = Color.Black,
    )

    val title1 = TextStyle(
        fontSize = 18.sp,
        fontFamily = rubikFamily,
        fontWeight = FontWeight(500),
        color = Color.Black,
    )

    val title2 = TextStyle(
        fontSize = 16.sp,
        fontFamily = rubikFamily,
        fontWeight = FontWeight(500),
        color = Color.Black,
    )

    val title3 = TextStyle(
        fontSize = 14.sp,
        fontFamily = rubikFamily,
        fontWeight = FontWeight.Medium,
        color = Color.Black,
    )

    val title4 = TextStyle(
        fontSize = 14.sp,
        fontFamily = rubikFamily,
        fontWeight = FontWeight.Normal,
        color = Color.Black,
    )

    val subtitle1 = TextStyle(
        fontSize = 12.sp,
        fontFamily = rubikFamily,
        fontWeight = FontWeight(400),
        color = Color.Black,
    )
}
