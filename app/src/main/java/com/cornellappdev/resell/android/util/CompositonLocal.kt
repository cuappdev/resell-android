package com.cornellappdev.resell.android.util

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController

val LocalNavigator = compositionLocalOf<NavHostController> { error("No navigator provided") }
