package com.cornellappdev.android.resell.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.cornellappdev.android.resell.ui.components.nav.NavBar

@Composable
fun TabScaffold() {
    val selectedScreen: MutableState<ResellScreen> = remember { mutableStateOf(ResellScreen.Home) }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = selectedScreen.value,
            label = "screen switch",
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(220, delayMillis = 90)
                ).togetherWith(
                    fadeOut(
                        animationSpec = tween(90)
                    )
                )
            }
        ) { state ->
            when (state) {
                ResellScreen.Home -> HomeScreen()
                else -> {
                    Text(
                        text = "Not implemented yet",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        NavBar(
            onHomeClick = { selectedScreen.value = ResellScreen.Home },
            onBookmarksClick = { selectedScreen.value = ResellScreen.Bookmarks },
            onMessagesClick = { selectedScreen.value = ResellScreen.Messages },
            onUserClick = { selectedScreen.value = ResellScreen.User },
            selectedTab = selectedScreen.value,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

sealed class ResellScreen {

    data object Home : ResellScreen()
    data object Bookmarks : ResellScreen()
    data object Messages : ResellScreen()
    data object User : ResellScreen()
}
