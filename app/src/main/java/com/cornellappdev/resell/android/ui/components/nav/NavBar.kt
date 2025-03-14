package com.cornellappdev.resell.android.ui.components.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.global.BrushIcon
import com.cornellappdev.resell.android.ui.screens.main.ResellMainScreen
import com.cornellappdev.resell.android.ui.theme.Padding
import com.cornellappdev.resell.android.ui.theme.animateResellBrush

@Composable
fun NavBar(
    selectedTab: ResellMainScreen,
    onHomeClick: () -> Unit,
    onBookmarksClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onUserClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val navigationBarsPadding =
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Box(modifier = modifier) {
        NavBarContent(
            onHomeClick = if (!enabled) {
                {}
            } else onHomeClick,
            selectedTab = selectedTab,
            onBookmarksClick = if (!enabled) {
                {}
            } else onBookmarksClick,
            onMessagesClick = if (!enabled) {
                {}
            } else onMessagesClick,
            onUserClick = if (!enabled) {
                {}
            } else onUserClick,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(navigationBarsPadding)
                .align(Alignment.BottomCenter)
                .background(Color.White)
        )
    }
}

@Composable
private fun NavBarContent(
    onHomeClick: () -> Unit,
    selectedTab: ResellMainScreen,
    onBookmarksClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onUserClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = false) {}
            .navigationBarsPadding()
            .height(55.dp),
        color = Color.White,
        shadowElevation = 10.dp,
        shape = RoundedCornerShape(topStart = Padding.xLarge, topEnd = Padding.xLarge),
    ) {
        // Content goes here
        Row(
            modifier = Modifier
                .padding(horizontal = 43.dp)
                .padding(top = Padding.large),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BrushIcon(
                painter = painterResource(id = R.drawable.ic_home),
                contentDescription = "home",
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = interactionSource
                    ) { onHomeClick() }
                    .size(27.dp),
                brush = animateResellBrush(targetGradient = selectedTab == ResellMainScreen.Home)
            )
            BrushIcon(
                painter = painterResource(id = R.drawable.ic_bookmark),
                contentDescription = "bookmarks",
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = interactionSource
                    ) { onBookmarksClick() }
                    .size(27.dp),
                brush = animateResellBrush(targetGradient = selectedTab == ResellMainScreen.Bookmarks)
            )
            BrushIcon(
                painter = painterResource(id = R.drawable.ic_messages),
                contentDescription = "messages",
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = interactionSource
                    ) { onMessagesClick() }
                    .size(27.dp),
                brush = animateResellBrush(targetGradient = selectedTab == ResellMainScreen.Messages)
            )
            BrushIcon(
                painter = painterResource(id = R.drawable.ic_user),
                contentDescription = "profile",
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = interactionSource
                    ) { onUserClick() }
                    .size(27.dp),
                brush = animateResellBrush(targetGradient = selectedTab == ResellMainScreen.User)
            )
        }
    }
}

@Preview
@Composable
private fun NavBarPreview() {
    val selectedTab: MutableState<ResellMainScreen> =
        remember { mutableStateOf(ResellMainScreen.Home) }

    NavBar(
        onHomeClick = { selectedTab.value = ResellMainScreen.Home },
        onBookmarksClick = { selectedTab.value = ResellMainScreen.Bookmarks },
        onMessagesClick = { selectedTab.value = ResellMainScreen.Messages },
        onUserClick = { selectedTab.value = ResellMainScreen.User },
        selectedTab = selectedTab.value
    )
}
