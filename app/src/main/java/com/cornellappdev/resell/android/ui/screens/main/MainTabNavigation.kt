package com.cornellappdev.resell.android.ui.screens.main

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.main.FloatingActionExpandingCTA
import com.cornellappdev.resell.android.ui.components.main.PostFloatingActionButton
import com.cornellappdev.resell.android.ui.components.main.ShadeOverlay
import com.cornellappdev.resell.android.ui.components.nav.NavBar
import com.cornellappdev.resell.android.ui.screens.main.ResellMainScreen.Home.toResellMainScreen
import com.cornellappdev.resell.android.util.LocalMainNavigator
import com.cornellappdev.resell.android.util.closeApp
import com.cornellappdev.resell.android.viewmodel.navigation.MainNavigationViewModel
import kotlinx.serialization.Serializable

@Composable
fun MainTabNavigation(
    mainNavigationViewModel: MainNavigationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val mainNav = mainNavigationViewModel.mainNavController
    val navDestination by mainNav.currentBackStackEntryAsState()
    val uiState = mainNavigationViewModel.collectUiStateValue()

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

            ShadeOverlay(
                onTapped = {
                    mainNavigationViewModel.onShadeTapped()
                },
                visible = uiState.newPostExpanded
            )

            FloatingActionContent(
                expanded = uiState.newPostExpanded,
                onRequestClick = {
                    mainNavigationViewModel.onNewRequestClick()
                },
                onPostClick = {
                    mainNavigationViewModel.onNewPostClick()
                },
                onExpandClick = {
                    mainNavigationViewModel.onNewPostExpandClick()
                },
                visible = listOf(
                    ResellMainScreen.Home,
                    ResellMainScreen.User,
                ).contains(navDestination?.destination?.route?.toResellMainScreen())
            )
        }
    }
}

@Composable
private fun BoxScope.FloatingActionContent(
    visible: Boolean,
    expanded: Boolean,
    onRequestClick: () -> Unit,
    onPostClick: () -> Unit,
    onExpandClick: () -> Unit,
) {
    val entireOpacity = animateFloatAsState(
        targetValue = if (visible) 1f else 0f, label = "opacity",
        animationSpec = tween(300)
    )
    val navigationBarsPadding =
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val expansionHeight = animateFloatAsState(
        targetValue = if (expanded) 1f else 0f, label = "expansion"
    )

    if (entireOpacity.value == 0f) return

    Box(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 26.dp, bottom = 70.dp)
            .padding(bottom = navigationBarsPadding)
            .alpha(entireOpacity.value),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionExpandingCTA(
            painter = painterResource(id = R.drawable.ic_wishlist_small),
            text = "New Request",
            expanded = expanded,
            onClick = { onRequestClick() },
            modifier = Modifier
                .padding(bottom = (expansionHeight.value * 140f).dp)
        )

        FloatingActionExpandingCTA(
            painter = painterResource(id = R.drawable.ic_shop_small),
            text = "New Listing",
            expanded = expanded,
            onClick = { onPostClick() },
            modifier = Modifier
                .padding(bottom = (expansionHeight.value * 80f).dp)
        )

        PostFloatingActionButton(
            expanded = expanded,
        ) {
            onExpandClick()
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
        return when (this.substringAfterLast('.')) {
            "Home" -> Home
            "Bookmarks" -> Bookmarks
            "Messages" -> Messages
            "User" -> User
            else -> Home
        }
    }
}
