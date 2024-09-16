package com.cornellappdev.android.resell.ui.components.nav

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.resell.R
import com.cornellappdev.android.resell.ui.components.global.BrushIcon
import com.cornellappdev.android.resell.ui.screens.main.ResellScreen
import com.cornellappdev.android.resell.ui.theme.Padding
import com.cornellappdev.android.resell.ui.theme.animateResellBrush

@Composable
fun NavBar(
    selectedTab: ResellScreen,
    onHomeClick: () -> Unit,
    onBookmarksClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onUserClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = false) {}
            .height(85.dp),
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
                brush = animateResellBrush(targetGradient = selectedTab == ResellScreen.Home)
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
                brush = animateResellBrush(targetGradient = selectedTab == ResellScreen.Bookmarks)
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
                brush = animateResellBrush(targetGradient = selectedTab == ResellScreen.Messages)
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
                brush = animateResellBrush(targetGradient = selectedTab == ResellScreen.User)
            )
        }
    }
}

@Preview
@Composable
private fun NavBarPreview() {
    val selectedTab: MutableState<ResellScreen> = remember { mutableStateOf(ResellScreen.Home) }

    NavBar(
        onHomeClick = { selectedTab.value = ResellScreen.Home },
        onBookmarksClick = { selectedTab.value = ResellScreen.Bookmarks },
        onMessagesClick = { selectedTab.value = ResellScreen.Messages },
        onUserClick = { selectedTab.value = ResellScreen.User },
        selectedTab = selectedTab.value
    )
}
