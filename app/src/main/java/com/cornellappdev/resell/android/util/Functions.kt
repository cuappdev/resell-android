package com.cornellappdev.resell.android.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap

/**
 * Returns if the bottom left is more black than white.
 */
fun isBottomLeftMoreBlack(imageBitmap: ImageBitmap): Boolean {
    // Get pixel data from the ImageBitmap
    val pixelMap = imageBitmap.toPixelMap()

    // Determine the area to inspect (50x50 or less, depending on image size)
    val width = minOf(50, pixelMap.width)
    val height = minOf(50, pixelMap.height)

    // Initialize counters for black-ish and white-ish pixels
    var blackCount = 0
    var whiteCount = 0

    // Iterate over the 50x50 area in the bottom left corner
    for (y in (pixelMap.height - height) until pixelMap.height) {
        for (x in 0 until width) {
            val pixelColor = pixelMap[x, y]

            // Convert pixel color to grayscale
            val grayscale =
                0.299 * pixelColor.red + 0.587 * pixelColor.green + 0.114 * pixelColor.blue

            // Decide if the pixel is more black or white (0 = black, 1 = white)
            if (grayscale < 0.5) {
                blackCount++
            } else {
                whiteCount++
            }
        }
    }

    // Return true if there are more black-ish pixels than white-ish pixels
    return blackCount > whiteCount
}

/**
 * If the string representing money has anything besides 2 decimal places, it will be formatted
 * to be 2 decimal places.
 *
 * If there are no decimal places, nothing is changed.
 */
fun String.formatMoney(): String {
    if (!this.contains(".")) {
        return this
    }

    val split = this.split(".")
    val dollars = split[0]
    var cents = split[1]

    if (cents.length == 0) {
        cents = "00"
    } else if (cents.length == 1) {
        cents += "0"
    } else if (cents.length > 2) {
        cents = cents.substring(0, 2)
    }

    return "$dollars.$cents"
}

/**
 * Returns if this string as a money value is less than the other string.
 */
fun String.isLeqMoney(other: String): Boolean {
    try {
        val thisMoney = this.formatMoney().toDouble()
        val otherMoney = other.formatMoney().toDouble()
        return thisMoney <= otherMoney
    } catch (e: Exception) {
        return false
    }
}
