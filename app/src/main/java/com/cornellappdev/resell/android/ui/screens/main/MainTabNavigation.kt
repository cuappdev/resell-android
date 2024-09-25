package com.cornellappdev.resell.android.ui.screens.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.ui.components.nav.NavBar
import com.cornellappdev.resell.android.ui.theme.simpleFadeInOut
import com.cornellappdev.resell.android.util.closeApp
import com.cornellappdev.resell.android.viewmodel.navigation.NavigationViewModel

@Composable
fun MainTabNavigation(
    navigationViewModel: NavigationViewModel = hiltViewModel()
) {
    val selectedScreen: MutableState<ResellMainScreen> =
        remember { mutableStateOf(ResellMainScreen.Home) }
    val context = LocalContext.current
    val mainNav = navigationViewModel.mainNavController

    // If on main tab, close the app.
    BackHandler {
        closeApp(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        AnimatedContent(
            targetState = selectedScreen.value,
            label = "screen switch",
            transitionSpec = simpleFadeInOut,
        ) { state ->
            when (state) {
                ResellMainScreen.Home -> HomeScreen()
                ResellMainScreen.Bookmarks -> SavedScreen()
                ResellMainScreen.Messages -> {

                }

                ResellMainScreen.User -> ProfileScreen()
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
