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
            val grayscale = 0.299 * pixelColor.red + 0.587 * pixelColor.green + 0.114 * pixelColor.blue

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
