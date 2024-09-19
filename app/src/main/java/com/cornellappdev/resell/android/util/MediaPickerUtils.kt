package com.cornellappdev.resell.android.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Makes a single photo picker. Still needs to have `.launch()` called.
 */
@Composable
fun singlePhotoPicker(onResult: (Uri?) -> Unit) =
    rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = onResult
    )

suspend fun loadBitmapFromUri(context: Context,  uri: Uri): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
