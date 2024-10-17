package com.cornellappdev.resell.android.ui.screens.main

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cornellappdev.resell.android.ui.components.nav.NavBar
import com.cornellappdev.resell.android.ui.screens.main.ResellMainScreen.Home.toResellMainScreen
import com.cornellappdev.resell.android.util.LocalMainNavigator
import com.cornellappdev.resell.android.util.closeApp
import com.cornellappdev.resell.android.viewmodel.navigation.NavigationViewModel
import kotlinx.serialization.Serializable

@Composable
fun MainTabNavigation(
    navigationViewModel: NavigationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val mainNav = navigationViewModel.mainNavController
    val navDestination by mainNav.currentBackStackEntryAsState()

    // If on main tab, close the app.
    BackHandler {
        closeApp(context)
    }

    CompositionLocalProvider(LocalMainNavigator provides mainNav) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            NavHost(
                navController = LocalMainNavigator.current,
                startDestination = ResellMainScreen.Home,
                modifier = Modifier.fillMaxSize()
            ) {
                composable<ResellMainScreen.Home> {
                    HomeScreen()
                }

                composable<ResellMainScreen.Bookmarks> {
                    SavedScreen()
                }

                composable<ResellMainScreen.Messages> {
                    MessagesScreen()
                }

                composable<ResellMainScreen.User> {
                    ProfileScreen()
                }
            }

            NavBar(
                onHomeClick = {
                    mainNav.navigate(ResellMainScreen.Home)
                },
                onBookmarksClick = {
                    mainNav.navigate(ResellMainScreen.Bookmarks)
                },
                onMessagesClick = {
                    mainNav.navigate(ResellMainScreen.Messages)
                },
                onUserClick = {
                    mainNav.navigate(ResellMainScreen.User)
                },
                selectedTab = navDestination?.destination?.route?.toResellMainScreen()
                    ?: ResellMainScreen.Home,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Serializable
sealed class ResellMainScreen {

    @Serializable
    data object Home : ResellMainScreen()

    @Serializable
    data object Bookmarks : ResellMainScreen()

    @Serializable
    data object Messages : ResellMainScreen()

    @Serializable
    data object User : ResellMainScreen()

    fun String.toResellMainScreen(): ResellMainScreen {
        Log.d("helpme", this.substringAfterLast('.'))
        return when (this.substringAfterLast('.')) {
            "Home" -> Home
            "Bookmarks" -> Bookmarks
            "Messages" -> Messages
            "User" -> User
            else -> Home
        }
    }
}
