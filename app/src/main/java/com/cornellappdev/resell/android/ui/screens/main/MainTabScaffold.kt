package com.cornellappdev.resell.android.ui.screens.main

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.cornellappdev.resell.android.ui.components.nav.NavBar

@Composable
fun MainTabScaffold() {
    val selectedScreen: MutableState<ResellMainScreen> =
        remember { mutableStateOf(ResellMainScreen.Home) }
    val context = LocalContext.current

    // If on main tab, close the app.
    BackHandler {
        // launch intent to home of android phone
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = selectedScreen.value,
            label = "screen switch",
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(300, delayMillis = 0)
                ).togetherWith(
                    fadeOut(
                        animationSpec = tween(300)
                    )
                )
            }
        ) { state ->
            when (state) {
                ResellMainScreen.Home -> HomeScreen()
                ResellMainScreen.Bookmarks -> SavedScreen()
                ResellMainScreen.Messages -> {

                }

                ResellMainScreen.User -> {

                }
            }
        }

        NavBar(
            onHomeClick = { selectedScreen.value = ResellMainScreen.Home },
            onBookmarksClick = { selectedScreen.value = ResellMainScreen.Bookmarks },
            onMessagesClick = { selectedScreen.value = ResellMainScreen.Messages },
            onUserClick = { selectedScreen.value = ResellMainScreen.User },
            selectedTab = selectedScreen.value,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

sealed class ResellMainScreen {

    data object Home : ResellMainScreen()
    data object Bookmarks : ResellMainScreen()
    data object Messages : ResellMainScreen()
    data object User : ResellMainScreen()
}
