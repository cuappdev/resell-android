package com.cornellappdev.resell.android.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
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
        fontFamily = reemKufiFamily,
        fontWeight = FontWeight(400),
        brush = ResellGradientVertical,
    )

    /**
     * The Resell title/brand text style.
     */
    val resellLogo = TextStyle(
        fontSize = 48.sp,
        fontFamily = reemKufiFamily,
        fontWeight = FontWeight(400),
        brush = ResellGradientLogo,
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

    val title2Gradient = TextStyle(
        fontSize = 16.sp,
        fontFamily = rubikFamily,
        fontWeight = FontWeight(500),
        brush = ResellGradientLogo,
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

    val appDev = TextStyle(
        fontSize = 17.sp,
        fontFamily = helveticaFamily,
        fontWeight = FontWeight(400),
        color = AppDev,
    )

    val overlay = TextStyle(
        fontSize = 17.sp,
        lineHeight = 22.sp,
        fontFamily = rubikFamily,
        fontWeight = FontWeight(400),
        color = Color(0xFF000000),
    )

    val noHeight = TextStyle(
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    )
}
