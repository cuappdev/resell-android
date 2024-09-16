package com.cornellappdev.resell.android.util

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController

val LocalNavController = compositionLocalOf<NavHostController> {
    throw IllegalStateException("LocalNavHostController not provided")
}
