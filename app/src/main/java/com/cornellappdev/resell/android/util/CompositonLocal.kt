package com.cornellappdev.resell.android.util

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.compositionLocalOf

val LocalInfiniteLoading = compositionLocalOf<Float> { error("No infinite loading provided") }

fun closeApp(context: Context) {
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_HOME)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(intent)
}
