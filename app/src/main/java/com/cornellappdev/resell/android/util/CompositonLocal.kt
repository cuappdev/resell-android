package com.cornellappdev.resell.android.util

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController

val LocalRootNavigator = compositionLocalOf<NavHostController> { error("No navigator provided") }
val LocalMainNavigator = compositionLocalOf<NavHostController> { error("No navigator provided") }
val LocalNewPostNavigator = compositionLocalOf<NavHostController> { error("No navigator provided") }
val LocalOnboardingNavigator =
    compositionLocalOf<NavHostController> { error("No navigator provided") }
val LocalSettingsNavigator =
    compositionLocalOf<NavHostController> { error("No navigator provided") }
val LocalReportNavigator = compositionLocalOf<NavHostController> { error("No navigator provided") }

fun closeApp(context: Context) {
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_HOME)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(intent)
}
